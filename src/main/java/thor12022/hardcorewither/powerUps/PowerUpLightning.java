package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;

@Configurable
class PowerUpLightning extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 1;
   
   @Config(minFloat = 1f, maxFloat = 5f)
   private static float lightningFrequencyMultiplier  = 1.1f;
   
   @Config(minFloat = 1f, maxFloat = 5f, comment = "0 is not random, 1 is more random")
   private static float lightningRandomness = 0.5f;
   
   @Config(minFloat = 1f, maxFloat = 5f, comment = "Avg number of ticks between lightning")
   private static int   lightningFequencyBase         = 100;
   
   @Config(minFloat = 0f, maxFloat = 5f, comment = "0 is prefect")
   private static float lightningInaccuracy = 0.5f;
   
   protected static class Data extends IPowerUpStateData
   {
      long nextTick;

      Data(long nextTick)
      {
         this.nextTick = nextTick;
      }
      
      @Override
      public NBTTagCompound serializeNBT()
      {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setLong("powerStrength", nextTick);
         return nbt;
      }

      @Override
      public void deserializeNBT(NBTTagCompound nbt)
      {
         nextTick = nbt.getLong("powerStrength");
      }
   }
   
   protected PowerUpLightning()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.config.register(this);
   }

   private static long setNextRandomTick(long currentTick, int strength)
   {
      int strengthBasedTick = (int) (lightningFequencyBase / (strength * lightningFrequencyMultiplier));
      int modifier = (int) ((HardcoreWither.RAND.nextGaussian() * lightningRandomness) * strengthBasedTick);
      return currentTick + strengthBasedTick + modifier;
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      Data lightningData = (Data)data;
      if( wither.worldObj.getTotalWorldTime() > lightningData.nextTick )
      {
         int targetId = wither.getWatchedTargetId(0);
         if( targetId != -1)
         {
            Entity target = wither.worldObj.getEntityByID(targetId);
            if(target != null)
            {
               double lightningXPos = target.lastTickPosX + (8 * HardcoreWither.RAND.nextGaussian() * lightningInaccuracy);
               double lightningYPos = target.lastTickPosY + (8 * HardcoreWither.RAND.nextGaussian() * lightningInaccuracy);
               double lightningZPos = target.lastTickPosZ + (8 * HardcoreWither.RAND.nextGaussian() * lightningInaccuracy);
               //! @todo I hear rumours that this causes the sound effect Server-Wide
               wither.worldObj.addWeatherEffect(new EntityLightningBolt(wither.worldObj, lightningXPos, lightningYPos, lightningZPos));
               lightningData.nextTick = setNextRandomTick(wither.worldObj.getTotalWorldTime(), strength);
            }
         }
      }
   }

   @Override
   public void witherDied(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public IPowerUpStateData applyPowerUp(EntityWither wither, int strength)
   {
      return new Data(setNextRandomTick(wither.worldObj.getTotalWorldTime(), strength));
   }
}
