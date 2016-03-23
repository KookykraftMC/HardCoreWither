package thor12022.hardcorewither.wither.powerups;

import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntityBlazeMinion;

@Configurable
class PowerUpBlazeMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 2;
   
   protected PowerUpBlazeMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, EntityBlazeMinion.LOCALIZED_NAME);
      HardcoreWither.CONFIG.register(this);
   }
   
   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      if(wither.getInvulTime() > 0)
      {
         super.updateWither(wither, strength, data);
      }
   }
}
