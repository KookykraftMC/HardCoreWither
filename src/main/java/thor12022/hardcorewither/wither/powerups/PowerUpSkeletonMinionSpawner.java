package thor12022.hardcorewither.wither.powerups;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntitySkeletonMinion;

@Configurable
public class PowerUpSkeletonMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   public PowerUpSkeletonMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, EntitySkeletonMinion.LOCALIZED_NAME);
      HardcoreWither.CONFIG.register(this);   
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      if(wither.isArmored() && wither.getInvulTime() <= 0)
      {
         super.updateWither(wither, strength, data);
      }
   }
}
