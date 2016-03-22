package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;

@Configurable
class PowerUpTeleport extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 4;
   
   @Config(minFloat = 1f, maxFloat = 10f)
   private static float teleportFrequencyMultiplier = 1.1f;
   
   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is not random, 1 is more random")
   private static float teleportRandomness = 0.5f;
   
   @Config(minInt = 20, comment = "Avg number of ticks between teleport")
   private static int   teleportFequencyBase = 100;
   
   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is prefect")
   private static float teleportInaccuracy = 0.25f;
   
   static class Data extends IPowerUpStateData
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
   
   
   protected PowerUpTeleport()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.config.register(this);   
   }
      
   private static boolean teleportTo(EntityWither wither, double x, double y, double z)
   {
       double oldX = wither.posX;
       double oldY = wither.posY;
       double oldZ = wither.posZ;
       
       wither.setPosition(x, y, z);
       
       if (!wither.worldObj.getCollidingBoundingBoxes(wither, wither.getEntityBoundingBox()).isEmpty())
       {
          wither.setPosition(oldX, oldY, oldZ);
          return false;
       }
       else
       {
          wither.setPosition(wither.posX, wither.posY, wither.posZ);
          wither.worldObj.playSoundEffect(oldX, oldY, oldZ, "mob.endermen.portal", 1.0F, 1.0F);
          //! @todo ensure this plays the sound on the client, I suspect not
          wither.playSound("mob.endermen.portal", 1.0F, 1.0F);
          return true;
       }
   }

   private static long setNextRandomTick(long currentTick, int strength)
   {
      int strengthBasedTick = (int) (teleportFequencyBase / (strength * teleportFrequencyMultiplier));
      int modifier = (int) ((HardcoreWither.RAND.nextGaussian() * teleportRandomness) * strengthBasedTick);
      return currentTick + strengthBasedTick + modifier;
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      Data stateData = (Data)data;
      if( wither.getInvulTime() <= 0 &&  wither.worldObj.getTotalWorldTime() > stateData.nextTick )
      {
         int targetId = wither.getWatchedTargetId(0);
         if( targetId != -1)
         {
            Entity target = wither.worldObj.getEntityByID(targetId);
            if(target != null)
            {
               int retryCount = 0;
               double teleportXPos = 0.0;
               double teleportYPos = 0.0;
               double teleportZPos = 0.0;
               int meanDistance = wither.getDistanceSqToEntity(target) >= 256D ? 16 : 8;
               double standardDeviation = 4 * teleportInaccuracy;
               do
               {
                  teleportXPos = target.lastTickPosX + (meanDistance * (HardcoreWither.RAND.nextBoolean() ? 1 : -1)) + (standardDeviation * HardcoreWither.RAND.nextGaussian());
                  teleportYPos = target.lastTickPosY + (meanDistance * (HardcoreWither.RAND.nextBoolean() ? 1 : -1)) + (standardDeviation * HardcoreWither.RAND.nextGaussian());
                  teleportZPos = target.lastTickPosZ + (meanDistance * (HardcoreWither.RAND.nextBoolean() ? 1 : -1)) + (standardDeviation * HardcoreWither.RAND.nextGaussian());
                  if(teleportYPos < 0)
                  {
                     teleportYPos = 0;
                  }
                  ++retryCount;
               } while(!teleportTo(wither, teleportXPos,teleportYPos, teleportZPos) && retryCount < 4);
               stateData.nextTick = setNextRandomTick(wither.worldObj.getTotalWorldTime(), strength); 
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
