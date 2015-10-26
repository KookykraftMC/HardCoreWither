package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;

class PowerUpHealthBoost extends AbstractPowerUp
{
   private static final float healthBoostMultiplier = 1.1f;
   
   protected PowerUpHealthBoost()
   {
      super();
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
      if(super.powerStrength < 20)
      {
         double health = ownerWither.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
         double newHealth = health * healthBoostMultiplier;
         ownerWither.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newHealth);
         // We need to adjust the charging time for the new health situation
         ownerWither.func_82215_s((int)(newHealth * (2.0F/3.0F)) + 20);
         ownerWither.setHealth((float)(newHealth) / 3.0F);
         return super.increasePower();
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public int minPower()
   {
      return 1;
   }
};
