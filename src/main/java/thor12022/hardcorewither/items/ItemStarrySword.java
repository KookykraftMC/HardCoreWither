package thor12022.hardcorewither.items;

import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.enchantment.EnchantmentRegistry;
import thor12022.hardcorewither.util.TextHelper;

@Configurable
public class ItemStarrySword extends ItemSword implements IItem
{
   private static final String NAME                = "starrySword";
   private static final String NBT_WITHER_AFFINITY = "witherAffinity";
   
   @Config
   private boolean isEnabled = true;
   
   @Config(minInt=1)
   private int levelingBase = 10;

   @Config(minFloat=1)
   private float levelingMultiplier = 2.5f;
   
   public ItemStarrySword()
   {
      super(MaterialHelper.witherEmerald);
      this.setUnlocalizedName(ModInformation.ID + "." + NAME);
      this.setCreativeTab(HardcoreWither.CREATIVE_TAB);
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
   
   protected int xpCalc(int currentLevel)
   {
      return currentLevel * (levelingBase + (int)((levelingBase - 1) * levelingMultiplier ));
   }
   
   protected boolean shouldWitherAffinityLevelUp(ItemStack stack, int currentLevel)
   {
      return getWitherAffinityXp(stack) >= xpCalc(currentLevel);
   }
   
   @Override
   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack)
   {
       return true;
   }

   @Override
   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
   {
      if(target instanceof EntityWither)
      {
         Map<Integer, Integer> enchantMap = EnchantmentHelper.getEnchantments(stack);
         Integer witherAffinityLevel = enchantMap.get(EnchantmentRegistry.witherAffinity.effectId);
         
         if(target.getHealth() <= 0)
         {
            if(witherAffinityLevel == null)
            {
               enchantMap.put(EnchantmentRegistry.witherAffinity.effectId, 1);
               EnchantmentHelper.setEnchantments(enchantMap, stack);
            }
         }
         else
         {
            setWitherAffinityXp(stack, getWitherAffinityXp(stack) + 1);
                        
            if(shouldWitherAffinityLevelUp(stack, witherAffinityLevel))
            {
               enchantMap.put(EnchantmentRegistry.witherAffinity.effectId, witherAffinityLevel + 1);
               EnchantmentHelper.setEnchantments(enchantMap, stack);
            }
         }
      }
      return super.hitEntity(stack, target, attacker);
   }

   @Override
   @SideOnly(Side.CLIENT)
   public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
   {
      list.add(TextHelper.GRAY + TextHelper.ITALIC + TextHelper.localize("tooltip." + ModInformation.ID + ".unbreaking" ));
      
      int witherAffinityLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.witherAffinity.effectId, stack);
      if(witherAffinityLevel > 0)
      {
         int witherAffinityXp = getWitherAffinityXp(stack);
         String witherAffinityLine = TextHelper.localize("tooltip." + ModInformation.ID + ".witherAffinity") + ": " +  witherAffinityXp + "/" + xpCalc(witherAffinityLevel);
         
         list.add(witherAffinityLine);
         list.add("");         
      }
   }
   
   @Override
   public int getMaxDamage()
   {
       return 0;
   }  
   
   @Override
   public boolean isDamageable()
   {
      return false;
   }
   
   @Override
   public boolean isItemTool(ItemStack stack)
   {
      return stack.getItem() instanceof ItemStarrySword;
   }

   @Override
   public final String name()
   {
      return NAME;
   }

   @Override
   public void registerItem()
   {
      GameRegistry.registerItem(this, NAME);
   }

   @Override
   public void registerModel()
   {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInformation.ID + ":" + NAME));
   }

   @Override
   public void registerRecipe()
   {}
   
   @Override
   public boolean isEnabled()
   {
      return isEnabled;
   }
}
