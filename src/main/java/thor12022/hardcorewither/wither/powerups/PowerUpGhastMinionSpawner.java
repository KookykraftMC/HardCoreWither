package thor12022.hardcorewither.wither.powerups;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntityGhastMinion;

@Configurable
public class PowerUpGhastMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   public PowerUpGhastMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, EntityGhastMinion.LOCALIZED_NAME);
      HardcoreWither.CONFIG.register(this);   
   }
   
   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      if(wither.getInvulTime() <= 0 && !wither.isArmored())
      {
         super.updateWither(wither, strength, data);
      }
   }
}
