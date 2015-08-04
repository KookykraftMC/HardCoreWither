package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.IConfigClass;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;

public class PowerUpSpeedBoost extends AbstractPowerUp implements IConfigClass
{
   private static int speedBoostMultiplier = 1;

   protected PowerUpSpeedBoost()
   {
      super();
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
   public int minPower()
   {
      return 3;
   }

   @Override
   public boolean increasePower()
   {
      if(powerStrength < 3)
      {
         boolean result = super.increasePower();
         if(result)
         {
            // @todo Increase Wither's speed
         }
         return result;
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
      speedBoostMultiplier = config.getInt("speedBoostMultiplier", this.getSectionName(), speedBoostMultiplier, 0, 10, "I'd really reccomend 1");
   }

}
