package thor12022.hardcorewither.powerUps;

import java.util.Random;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

@Configurable
class PowerUpTeleport extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 4;
   private static final Random random = new Random();
   
   @Config(minFloat = 1f, maxFloat = 10f)
   private static float teleportFrequencyMultiplier = 1.1f;
   
   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is not random, 1 is more random")
   private static float teleportRandomness = 0.5f;
   
   @Config(minInt = 20, comment = "Avg number of ticks between teleport")
   private static int   teleportFequencyBase = 100;
   
   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is prefect")
   private static float teleportInaccuracy = 0.25f;
   
   private long   teleportNextTick;
   
   
   protected PowerUpTeleport()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().register(this);   
   }
   
   private PowerUpTeleport(EntityWither theOwnerWither)
   {
      super(theOwnerWither);
      increasePower();
   }

   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpTeleport powerUp = new PowerUpTeleport(theOwnerWither);
      return powerUp;
   }
   
   @Override
   public void updateWither()
   {
      if( ownerWither.worldObj.getTotalWorldTime() > teleportNextTick )
      {
         int targetId = ownerWither.getWatchedTargetId(0);
         if( targetId != -1)
         {
            Entity target = ownerWither.worldObj.getEntityByID(targetId);
            if(target != null)
            {
               int retryCount = 0;
               double teleportXPos = 0.0;
               double teleportYPos = 0.0;
               double teleportZPos = 0.0;
               int meanDistance = ownerWither.getDistanceSqToEntity(target) >= 256D ? 16 : 8;
               double standardDeviation = 4 * teleportInaccuracy;
               do
               {
                  teleportXPos = target.lastTickPosX + (meanDistance * (random.nextBoolean() ? 1 : -1)) + (standardDeviation * random.nextGaussian());
                  teleportYPos = target.lastTickPosY + (meanDistance * (random.nextBoolean() ? 1 : -1)) + (standardDeviation * random.nextGaussian());
                  teleportZPos = target.lastTickPosZ + (meanDistance * (random.nextBoolean() ? 1 : -1)) + (standardDeviation * random.nextGaussian());
                  if(teleportYPos < 0)
                  {
                     teleportYPos = 0;
                  }
                  ++retryCount;
               } while(!teleportTo(teleportXPos,teleportYPos, teleportZPos) && retryCount < 4);
               setNextRandomTick(); 
            }
         }
      }
   }
   
   protected boolean teleportTo(double x, double y, double z)
   {
       double oldX = ownerWither.posX;
       double oldY = ownerWither.posY;
       double oldZ = ownerWither.posZ;
       
       ownerWither.setPosition(x, y, z);
       
       if (!ownerWither.worldObj.getCollidingBoundingBoxes(ownerWither, ownerWither.getEntityBoundingBox()).isEmpty())
       {
           ownerWither.setPosition(oldX, oldY, oldZ);
           return false;
       }
       else
       {
           ownerWither.setPosition(ownerWither.posX, ownerWither.posY, ownerWither.posZ);
           ownerWither.worldObj.playSoundEffect(oldX, oldY, oldZ, "mob.endermen.portal", 1.0F, 1.0F);
           ownerWither.playSound("mob.endermen.portal", 1.0F, 1.0F);
           return true;
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
   
   private void setNextRandomTick()
   {
      int strengthBasedTick = (int) (teleportFequencyBase / (super.powerStrength * teleportFrequencyMultiplier));
      int modifier = (int) ((random.nextGaussian() * teleportRandomness) * strengthBasedTick);
      teleportNextTick =  ownerWither.worldObj.getTotalWorldTime() + strengthBasedTick + modifier;
   }

};
