package thor12022.hardcorewither.wither;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.api.WitherAffinityRegistry;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.enchantment.EnchantmentRegistry;
import thor12022.hardcorewither.util.TextHelper;
import thor12022.hardcorewither.wither.powerups.PowerUpHelper;

@Configurable(sectionName="WitherAffinity")
public class WitherAffinityHandler
{
   private static final String NBT_WITHER_AFFINITY = "witherAffinity";
   
   @Config
   private boolean isEnabled = true;
   
   @Config(minInt=1)
   private int levelingBase = 300;

   @Config(minFloat=1f)
   private float levelingMultiplier = 2.5f;
   
   @Config(comment="is s, the enchant level is l, and the chance is c: where s/(-1*l-s) = c")
   private float powerUpRemovalChanceScale = 12f;
   
   public WitherAffinityHandler()
   {
      MinecraftForge.EVENT_BUS.register(this);
   }
   
   protected int getWitherAffinityXp(ItemStack stack)
   {
      NBTTagCompound tag = stack.getTagCompound();
      return tag.getInteger(NBT_WITHER_AFFINITY);
   }
   
   protected void setWitherAffinityXp(ItemStack stack, int xp)
   {
      NBTTagCompound tag = stack.getTagCompound();
      tag.setInteger(NBT_WITHER_AFFINITY, xp);
   }
   
   private float chanceCalc(int currentLevel)
   {
      return powerUpRemovalChanceScale / (-1 * currentLevel - powerUpRemovalChanceScale);
   }
   
   private int xpCalc(int currentLevel)
   {
      return currentLevel * (levelingBase + (int)((levelingBase - 1) * levelingMultiplier ));
   }
   
   private boolean shouldWitherAffinityLevelUp(ItemStack stack, int currentLevel)
   {
      return getWitherAffinityXp(stack) >= xpCalc(currentLevel);
   }
   
   @SubscribeEvent
   public void onTooltipEvent(ItemTooltipEvent event)
   {
      int witherAffinityLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.witherAffinity.effectId, event.itemStack);
      if(witherAffinityLevel > 0)
      {
         int witherAffinityXp = getWitherAffinityXp(event.itemStack);
         String witherAffinityLine = TextHelper.localize("tooltip." + ModInformation.ID + ".witherAffinity") + ": " +  witherAffinityXp + "/" + xpCalc(witherAffinityLevel);
         
         event.toolTip.add(witherAffinityLine);
         event.toolTip.add("");         
      }
   }
   
   @SubscribeEvent
   public void onLivingHurtEvent(LivingHurtEvent event)
   {
      if(event.entity instanceof EntityWither && 
         event.source.getEntity() instanceof EntityPlayer)
      {
         EntityPlayer player = (EntityPlayer) event.source.getEntity();
         ItemStack weaponStack = player.getHeldItem();
         if(WitherAffinityRegistry.isRegistered(weaponStack.getItem()))
         {
            Map<Integer, Integer> enchantMap = EnchantmentHelper.getEnchantments(weaponStack);
            Integer witherAffinityLevel = enchantMap.get(EnchantmentRegistry.witherAffinity.effectId);
            
            if(event.entityLiving.getHealth() <= 0)
            {
               if(witherAffinityLevel == null)
               {
                  enchantMap.put(EnchantmentRegistry.witherAffinity.effectId, 1);
                  EnchantmentHelper.setEnchantments(enchantMap, weaponStack);
               }
            }
            else
            {
               setWitherAffinityXp(weaponStack, getWitherAffinityXp(weaponStack) + Math.round(event.ammount));
                           
               if(shouldWitherAffinityLevelUp(weaponStack, witherAffinityLevel))
               {
                  enchantMap.put(EnchantmentRegistry.witherAffinity.effectId, witherAffinityLevel + 1);
                  EnchantmentHelper.setEnchantments(enchantMap, weaponStack);
               }
               
               // Roll under the target for 2nd Ed flashbacks
               if(HardcoreWither.RAND.nextFloat() < chanceCalc(witherAffinityLevel))
               {
                  PowerUpHelper.reduceWitherPowerUp((EntityWither) event.entity);
               }
            }
         }
      }
   }
}
