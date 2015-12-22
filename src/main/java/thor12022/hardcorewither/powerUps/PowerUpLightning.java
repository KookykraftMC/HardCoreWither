package thor12022.hardcorewither.powerUps;

import java.util.Random;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;

@Configurable
class PowerUpLightning extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 1;
   private final static Random random = new Random();
   
   @Config(minFloat = 1f, maxFloat = 5f)
   private static float lightningFrequencyMultiplier  = 1.1f;
   
   @Config(minFloat = 1f, maxFloat = 5f, comment = "0 is not random, 1 is more random")
   private static float lightningRandomness = 0.5f;
   
   @Config(minFloat = 1f, maxFloat = 5f, comment = "Avg number of ticks between lightning")
   private static int   lightningFequencyBase         = 100;
   
   @Config(minFloat = 0f, maxFloat = 5f, comment = "0 is prefect")
   private static float lightningInaccuracy = 0.5f;
   
   private long   lightningNextTick;
   
   protected PowerUpLightning()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().register(this);
   }
   
   private PowerUpLightning(EntityWither theOwnerWither)
   {
      super(theOwnerWither);
      setNextRandomTick();
   }

   private void setNextRandomTick()
   {
      int strengthBasedTick = (int) (lightningFequencyBase / (super.powerStrength * lightningFrequencyMultiplier));
      int modifier = (int) ((random.nextGaussian() * lightningRandomness) * strengthBasedTick);
      lightningNextTick =  ownerWither.worldObj.getTotalWorldTime() + strengthBasedTick + modifier;
   }
   
   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpLightning powerUp = new PowerUpLightning(theOwnerWither);
      return powerUp;
   }

   @Override
   public void updateWither()
   {
      if( ownerWither.worldObj.getTotalWorldTime() > lightningNextTick )
      {
         int targetId = ownerWither.getWatchedTargetId(0);
         if( targetId != -1)
         {
            Entity target = ownerWither.worldObj.getEntityByID(targetId);
            if(target != null)
            {
               double lightningXPos = target.lastTickPosX + (8 * random.nextGaussian() * lightningInaccuracy);
               double lightningYPos = target.lastTickPosY + (8 * random.nextGaussian() * lightningInaccuracy);
               double lightningZPos = target.lastTickPosZ + (8 * random.nextGaussian() * lightningInaccuracy);
               ownerWither.worldObj.addWeatherEffect(new EntityLightningBolt(ownerWither.worldObj, lightningXPos, lightningYPos, lightningZPos));
               setNextRandomTick();
            }
         }
      }
   }

   @Override
   public void witherDied()
   {}

   @Override
   public boolean increasePower() 
   {
      if(super.increasePower())
      {
         setNextRandomTick();
         return true;
      }
      else
      {
         return false;
      }
   }
};
