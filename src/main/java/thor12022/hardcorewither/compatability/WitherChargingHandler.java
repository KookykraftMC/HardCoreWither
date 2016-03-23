package thor12022.hardcorewither.compatability;

import java.lang.reflect.Field;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;

@Configurable
public class WitherChargingHandler
{
   @Config(comment="MC-57569 Wither Moves During Shield Generation / Wither attacking before initial explosion")
   private static boolean vanillaBugFix = true;
   
   private static Predicate<Entity> attackEntitySelector = null;
 
   public WitherChargingHandler()
   {
     HardcoreWither.CONFIG.register(this);
     if(!Loader.isModLoaded("witherchargingfix"))
     {
        MinecraftForge.EVENT_BUS.register(this);
     }
     else
     {
        HardcoreWither.LOGGER.info("Wither Charging Fix Mod loaded, disabling internal Wither Charging Fix");
     }
   }
   
   @SubscribeEvent(priority=EventPriority.LOW)
   public void onLivingUpdate(LivingUpdateEvent event)
   {
      if(vanillaBugFix && 
         !event.entity.worldObj.isRemote &&
         event.entityLiving != null &&
         event.entityLiving.getClass() == EntityWither.class)
      {
         EntityWither wither = (EntityWither)event.entityLiving;
         if(wither.getInvulTime() == 1)
         {
            if(attackEntitySelector == null)
            {
               // We can't retrieve the field from EntityWither at construction because Java doesn't initialize
               //    static data until an instance of the class has been created. A design decision I won't
               //    say anything about, I'll just leave you with these ellipses. . .
               try
               {
                  Field attackEntitySelectorField = EntityWither.class.getDeclaredField("field_82219_bJ");
                  attackEntitySelectorField.setAccessible(true);
                  attackEntitySelector = (Predicate<Entity>) attackEntitySelectorField.get(null);
               }
               catch(Exception e)
               {
                  try
                  {
                     Field attackEntitySelectorField = EntityWither.class.getDeclaredField("attackEntitySelector");
                     attackEntitySelectorField.setAccessible(true);
                     attackEntitySelector = (Predicate<Entity>) attackEntitySelectorField.get(null);
                  }
                  catch(Exception excp)
                  {
                     vanillaBugFix = false;
                     HardcoreWither.LOGGER.warn("Cannot get Wither Attack Selector Field, disabling Vanilla Wither Bug Fix");
                  }
               }
            }
            wither.targetTasks.addTask(1, new EntityAIHurtByTarget(wither, false, new Class[0]));
            wither.targetTasks.addTask(2, new EntityAINearestAttackableTarget(wither, EntityLiving.class, 0, false, false, attackEntitySelector));
         }
      }
   }
   
   @SubscribeEvent(priority=EventPriority.HIGHEST)
   public void onEntityJoinWorld(EntityJoinWorldEvent event)
   {
      if(vanillaBugFix &&
         !event.entity.worldObj.isRemote &&
         event.entity != null)
      {
         // Strip the Wither of it's targeting AI at spawn 
         if(event.entity.getClass() == EntityWither.class)
         {
            EntityWither wither = (EntityWither) event.entity;
            wither.targetTasks.taskEntries.clear();
         }
         // If we've loaded up a world with a Wither already firing away
         else if(event.entity.getClass() == EntityWitherSkull.class &&
               ((EntityWitherSkull)event.entity).shootingEntity != null &&
               ((EntityWitherSkull)event.entity).shootingEntity.getClass() == EntityWither.class && 
               ((EntityWither)((EntityWitherSkull)event.entity).shootingEntity).getInvulTime() > 0 )
         {
            event.setCanceled(true);
         }
      }         
   }
}
