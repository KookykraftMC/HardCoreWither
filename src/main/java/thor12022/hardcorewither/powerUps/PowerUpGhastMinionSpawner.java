package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntityGhastMinion;

@Configurable
class PowerUpGhastMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   protected PowerUpGhastMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.config.register(this);   
   }
   
   private PowerUpGhastMinionSpawner(EntityWither theOwnerWither)
   {
      super(theOwnerWither, EntityGhastMinion.LOCALIZED_NAME);
      super.spawnRange = 16;
      super.ResetSpawnerToData();
   }

   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpGhastMinionSpawner powerUpGhastMinionSpawner = new PowerUpGhastMinionSpawner(theOwnerWither);
      return powerUpGhastMinionSpawner;
   }

   @Override
   public void updateWither()
   {
      if(ownerWither.getInvulTime() <= 0 && !ownerWither.isArmored())
      {
         super.updateWither();
      }
   }

   @Override
   public void witherDied()
   {}
};
