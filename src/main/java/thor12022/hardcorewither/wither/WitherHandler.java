package thor12022.hardcorewither.wither;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.api.WitherAffinityRegistry;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.enchantment.EnchantmentRegistry;
import thor12022.hardcorewither.handlers.PlayerHandler;
import thor12022.hardcorewither.interfaces.INBTStorageClass;
import thor12022.hardcorewither.util.TextHelper;

@Configurable(sectionName="Wither")
public class WitherHandler implements INBTStorageClass
{
   private static final int NBT_FORMAT = 2;

   @Config(comment="Will enable the Looting enchant on weapons to affect the Wither's drops")
   private boolean witherLooting = true;
   
   @Config
   private boolean netherStarLooting = true;
   
   @Config(minFloat = 0f, comment="Scales the amount of reward in relation to Wither strength (non-linear)")
   private float lootingLevelMultiplier = 2f;
   
   @Config(minFloat=0f)
   private float netherStarLootingMultiplier = .5f;
   
   private int largestPowerUp;
   
   private PlayerHandler   playerHandler  =  new PlayerHandler();
   
   public WitherHandler()
   {
      HardcoreWither.DATA_STORE.addStorageClass(this, "witherData");
      
      HardcoreWither.CONFIG.register(this);
      HardcoreWither.CONFIG.register(WitherAffinityHelper.class);
      MinecraftForge.EVENT_BUS.register(this);
      
      PowerUpHelper.initialize();
   }
   
   @SubscribeEvent
   public void onMobConstructing(EntityConstructing event)
   {
      if(!event.entity.worldObj.isRemote)
      {
         if (event.entity != null && event.entity.getClass() == EntityWither.class)
         {
            WitherData.register((EntityWither)event.entity);
         }
      }
   }
   
   @SubscribeEvent
   public void onSpawnMob(EntityJoinWorldEvent event)
   {
      if(!event.world.isRemote)
      {
         if (event.entity != null && event.entity.getClass() == EntityWither.class)
         {
            EntityWither theWither = (EntityWither)event.entity;
            WitherData witherData = WitherData.getWitherData(theWither);
            if(!witherData.isPoweredUp())
            {
               List nearbyPlayers = theWither.worldObj.getEntitiesWithinAABB(EntityPlayer.class, theWither.getEntityBoundingBox().expand(64.0D, 64.0D, 64.0D));
               double powerUpSize = 0.0;
               for (int index = 0; index < nearbyPlayers.size(); ++index)
               {
                  EntityPlayer player = (EntityPlayer)nearbyPlayers.get(index);
                  powerUpSize += playerHandler.wasAtWitherSpawn(player);
               }
               if(powerUpSize > largestPowerUp)
               {
                  largestPowerUp = (int) Math.round(powerUpSize);
               }
               if(nearbyPlayers.size() == 0)
               {
                  powerUpSize = ++largestPowerUp;
               }
               PowerUpHelper.powerUpWither(witherData, (int)Math.round(powerUpSize));   
            }
         }
      }
   }
   
   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event)
   {
      if(!event.entity.worldObj.isRemote)
      {
         if (event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
         {
            WitherData data = WitherData.getWitherData((EntityWither) event.entityLiving);
            if(data != null)
            {
               data.update();
            }
         }
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
               WitherAffinityHelper.setWitherAffinityXp(weaponStack, WitherAffinityHelper.getWitherAffinityXp(weaponStack) + Math.round(event.ammount));
                           
               if(WitherAffinityHelper.shouldWitherAffinityLevelUp(weaponStack, witherAffinityLevel))
               {
                  enchantMap.put(EnchantmentRegistry.witherAffinity.effectId, witherAffinityLevel + 1);
                  EnchantmentHelper.setEnchantments(enchantMap, weaponStack);
               }
               
               // Roll under the target for 2nd Ed flashbacks
               if(HardcoreWither.RAND.nextFloat() < WitherAffinityHelper.chanceCalc(witherAffinityLevel))
               {
                  WitherData witherData = WitherData.getWitherData((EntityWither) event.entity);
                  if(witherData != null)
                  {
                     PowerUpHelper.reduceWitherPowerUp(witherData);
                  }
               }
            }
         }
      }
   }
   
   @SubscribeEvent
   public void witherLootDrops(LivingDropsEvent event)
   {
      if(!event.entity.worldObj.isRemote && event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
      {
         WitherData witherData = WitherData.getWitherData((EntityWither) event.entityLiving);
         // If we have not seen this Wither Die yet
         if(witherData != null && !witherData.isPendingLootDrops())
         {
            witherData.setPendingLootDrops(true);
            // Recalculate the looting based upon Wither strength 
            int lootingLevel = (witherLooting ? event.lootingLevel : 0) + (int)Math.round( Math.log10(witherData.getStrength() + 1) * lootingLevelMultiplier);
            // and resend the drops event
            if(!ForgeHooks.onLivingDrops(event.entityLiving, event.source, (ArrayList)event.drops, lootingLevel, event.recentlyHit))
            {
               //then do the same thing EntityLivingBase does
               for (EntityItem item : event.drops)
               {
                  event.entityLiving.worldObj.spawnEntityInWorld(item);
               }
               //! @note I don't like doing this, as far as the Forge Event manager is concerned the death event was cancelled
               //!   and that could have some side effects. However, the only other way I can think of at the moment is to 
               //!   use Reflection to modify the lootingLevel field of the LivingDropsEvent, which seems even worse.
            }
            // then cancel this one
            event.setCanceled(true);
         }
         // This is the resent event
         else if(witherData != null)
         {
            witherData.setPendingLootDrops(false);
            
            if (netherStarLooting && event.lootingLevel > 0)
            {
                int j = 0;
   
                j += Math.round(Math.abs(HardcoreWither.RAND.nextGaussian() * event.lootingLevel * netherStarLootingMultiplier));
   
                if( j > 0)
                {
                   EntityItem entityItem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(Items.nether_star, j, 0));
                   entityItem.setPickupDelay(10);
                   event.drops.add(entityItem);
                }
            }
         }
      }
   }
   
   @SubscribeEvent
   public void onEntityDieing(LivingDeathEvent event)
   {
      if(!event.entity.worldObj.isRemote)
      {
         if (event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
         {
            WitherData witherData = WitherData.getWitherData((EntityWither) event.entityLiving);
            if(witherData != null)
            {
               witherData.died();
               
               List nearbyPlayers = event.entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, event.entity.getEntityBoundingBox().expand(64.0D, 64.0D, 64.0D));
               for (int index = 0; index < nearbyPlayers.size(); ++index)
               {
                  EntityPlayer player = (EntityPlayer)nearbyPlayers.get(index);
                  playerHandler.wasAtWitherSpawn(player);
                  player.addChatMessage(new ChatComponentText(TextHelper.localize("info." + ModInformation.ID + ".chat.wither-experience")));
               }
            }
         }
      }
   }
   

   @SubscribeEvent
   public void onTooltipEvent(ItemTooltipEvent event)
   {
      int witherAffinityLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.witherAffinity.effectId, event.itemStack);
      if(witherAffinityLevel > 0)
      {
         int witherAffinityXp = WitherAffinityHelper.getWitherAffinityXp(event.itemStack);
         String witherAffinityLine = TextHelper.localize("tooltip." + ModInformation.ID + ".witherAffinity") + ": " +  witherAffinityXp + "/" + WitherAffinityHelper.xpCalc(witherAffinityLevel);
         
         event.toolTip.add(witherAffinityLine);
         event.toolTip.add("");         
      }
   }
   
   /**
    * ROOT
    * +--formatVersion : n
    * +--largestPowerUp : n  
    */
   @Override
   public NBTTagCompound serializeNBT()
   {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("formatVersion", NBT_FORMAT);
      nbt.setInteger("largestPowerUp", largestPowerUp );
      return nbt;
   }

   @Override
   public void deserializeNBT(NBTTagCompound nbt)
   {
      int formatVersion = nbt.getInteger("formatVersion");
      if(formatVersion < NBT_FORMAT)
      {
         HardcoreWither.LOGGER.warn("Detected old version of saved data. Withers' abilities will not convert to ", ModInformation.VERSION, " from older versions");
      }
      largestPowerUp = nbt.getInteger("largestPowerUp");      
   }  

   @Override
   public void resetNBT()
   {
      largestPowerUp =  0;      
   }
}
