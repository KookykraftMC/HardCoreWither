package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import net.minecraftforge.common.config.Configuration;

class PowerUpHealthBoost extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;
   
   private static float healthBoostMultiplier = 1.1f;
   
   protected PowerUpHealthBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
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
   {}

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
         // We need to adjust the charging time for the new health situation
         ownerWither.func_82215_s((int)(newHealth * (2.0F/3.0F)) + 20);
         ownerWither.setHealth((float)(newHealth) / 3.0F);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public String getSectionName()
   {
      return "PowerUpHealthBoost";
   }
   
   @Override
   public void syncConfig(Configuration config)
   {
      super.syncConfig(config);
      healthBoostMultiplier = config.getFloat("healthBoostMultiplier", this.getSectionName(), healthBoostMultiplier, 1.0f, 10.0f, "");
   }
};
