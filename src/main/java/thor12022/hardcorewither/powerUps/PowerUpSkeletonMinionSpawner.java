package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.entity.EntitySkeletonMinion;

class PowerUpSkeletonMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   protected PowerUpSkeletonMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
   }
   
   private PowerUpSkeletonMinionSpawner(EntityWither theOwnerWither)
   {
      super(theOwnerWither, EntitySkeletonMinion.LOCALIZED_NAME);
   }

   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpSkeletonMinionSpawner powerUpSkeletonMinionSpawner = new PowerUpSkeletonMinionSpawner(theOwnerWither);
      return powerUpSkeletonMinionSpawner;
   }

   @Override
   public void updateWither()
   {
      if(ownerWither.isArmored() && ownerWither.func_82212_n() <= 0)
      {
         super.updateWither();
      }
   }

   @Override
   public void witherDied()
   {}

   @Override
   public String getSectionName()
   {
      return "PowerUpSkeletonMinionSpawner";
   }
};
