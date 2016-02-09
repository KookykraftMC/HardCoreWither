package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.boss.EntityWither;

@Configurable
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

   private PowerUpSpeedBoost(EntityWither theOwnerWither)
   {
      super(theOwnerWither);
      increasePower();
   }

  @Override
   public void updateWither()
   {}

    @Override
   public void witherDied()
   {}

  @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      return new PowerUpSpeedBoost(theOwnerWither);
   }

   @Override
   public boolean increasePower()
   {
      if(super.increasePower())
      {
         // @todo Increase Wither's speed
         return true;
      }
      return false;
   }
}
