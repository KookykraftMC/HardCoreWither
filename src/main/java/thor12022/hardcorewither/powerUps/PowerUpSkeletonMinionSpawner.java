package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntitySkeletonMinion;

@Configurable
class PowerUpSkeletonMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   protected PowerUpSkeletonMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().register(this);   
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
      if(ownerWither.isArmored() && ownerWither.getInvulTime() <= 0)
      {
         super.updateWither();
      }
   }

   @Override
   public void witherDied()
   {}
};
