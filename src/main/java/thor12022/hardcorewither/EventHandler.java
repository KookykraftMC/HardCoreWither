package thor12022.hardcorewither;

/*
 * Class for most of your events to be registered in.
 * Remember that there are two different registries for Events. This one will not work for everything.
 */

import java.util.List;

import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.handlers.PlayerHandler;
import thor12022.hardcorewither.powerUps.PowerUpManager;
import thor12022.hardcorewither.util.TextHelper;

public class EventHandler
{
   private PlayerHandler playerHandler;
   private PowerUpManager powerUpManager;

   public EventHandler(PlayerHandler playerHandler, PowerUpManager powerUpManager)
   {
      this.playerHandler = playerHandler;
      this.powerUpManager = powerUpManager;
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
   {
      //! @todo pretty sure this event is invalid if there are multiple config files
      if(eventArgs.modID.equals(ModInformation.ID))
      {
         ConfigManager.getInstance().syncConfig();
         HardcoreWither.logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.config.refresh"));
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
            if( !powerUpManager.isWitherPoweredUp(theWither) )
            {
               List nearbyPlayers = theWither.worldObj.getEntitiesWithinAABB(EntityPlayer.class, theWither.getEntityBoundingBox().expand(64.0D, 64.0D, 64.0D));
               if(nearbyPlayers.size() > 0)
               {
                  double powerUpSize = 0.0;
                  for (int index = 0; index < nearbyPlayers.size(); ++index)
                  {
                     EntityPlayer player = (EntityPlayer)nearbyPlayers.get(index);
                     powerUpSize += playerHandler.wasAtWitherSpawn(player);
                  }
                  powerUpManager.powerUpWither(theWither, (int)Math.round(powerUpSize));
               }
               else
               {
                  powerUpManager.powerUpWither(theWither, -1);
               }
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
            powerUpManager.update((EntityWither) event.entityLiving);
            
         }
      }
   }
   
   @SubscribeEvent(priority=EventPriority.HIGHEST)
   public void onEntityDieing(LivingDropsEvent event)
   {
      if(!event.entity.worldObj.isRemote && event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
      {
         powerUpManager.witherLootDrops(event);
      }
   }
   
   @SubscribeEvent
   public void onEntityDieing(LivingDeathEvent event)
   {
      if(!event.entity.worldObj.isRemote)
      {
         if (event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
         {
            powerUpManager.witherDied((EntityWither)event.entityLiving);
            
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
