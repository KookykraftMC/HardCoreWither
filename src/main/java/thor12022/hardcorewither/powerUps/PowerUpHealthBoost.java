package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;

@Configurable
class PowerUpHealthBoost extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;

   @Config(minFloat = 1f, maxFloat = 10f)
   private static float healthBoostMultiplier = 1.1f;

   protected PowerUpHealthBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().register(this);
   }

   private PowerUpHealthBoost(EntityWither theOwnerWither)
   {
      super(theOwnerWither);
      increasePower();
   }

   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpHealthBoost powerUp = new PowerUpHealthBoost(theOwnerWither);
      return powerUp;
   }

   @Override
   public void updateWither()
   {
      if(ownerWither.isServerWorld())
      {
         if(ownerWither.getInvulTime() >= 20)
         {
            float baseHealth = (float) ownerWither.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
            // ! @todo this is close, but not quite right, it is a bit too fast
            // at higher levels
            ownerWither.heal((((baseHealth * (2f / 3f)) - 200) / 200));
         }
      }
   }

   @Override
   public void witherDied()
   {}

   @Override
   public boolean increasePower()
   {
      if(super.increasePower())
      {
         double health = ownerWither.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
         double newHealth = health * healthBoostMultiplier;
         ownerWither.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newHealth);
         return true;
      }
      else
      {
         return false;
      }
   }
};
