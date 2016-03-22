package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.boss.EntityWither;

@Configurable // This has no @Config member, but it's parent class does
public class PowerUpSpeedBoost extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 6;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   @Config(comment="I'd really reccomend 1")
   private static int speedBoostMultiplier = 1;

   protected PowerUpSpeedBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.config.register(this);
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public void witherDied(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public IPowerUpStateData applyPowerUp(EntityWither wither, int strength)
   {
      return null;
   }
}
