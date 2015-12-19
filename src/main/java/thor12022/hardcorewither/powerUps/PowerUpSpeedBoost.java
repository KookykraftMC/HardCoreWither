package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.IConfigClass;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;

public class PowerUpSpeedBoost extends AbstractPowerUp implements IConfigClass
{
   private final static int DEFAULT_MAX_STRENGTH = 6;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   private static int speedBoostMultiplier = 1;

   protected PowerUpSpeedBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().addConfigClass(this);
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

   @Override
   public String getSectionName()
   {
      return "PowerUpSpeedBoost";
   }

   @Override
   public void syncConfig(Configuration config)
   {
      super.syncConfig(config);
      speedBoostMultiplier = config.getInt("speedBoostMultiplier", this.getSectionName(), speedBoostMultiplier, 0, 10, "I'd really reccomend 1");
   }

}
