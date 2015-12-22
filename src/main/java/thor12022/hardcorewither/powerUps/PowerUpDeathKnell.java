package thor12022.hardcorewither.powerUps;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;

@Configurable
class PowerUpDeathKnell extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 1;
   
   @Config(minFloat = 0f, maxFloat = 10f)
   private static float knellStrengthMultiplier = 0.6666667f;
   
   protected PowerUpDeathKnell()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      ConfigManager.getInstance().register(this);   
   }
   
   private PowerUpDeathKnell(EntityWither theOwnerWither)
   {
      super(theOwnerWither);
      increasePower();
   }

   @Override
   public IPowerUp createPowerUp(EntityWither theOwnerWither)
   {
      PowerUpDeathKnell powerUp = new PowerUpDeathKnell(theOwnerWither);
      return powerUp;
   }

   @Override
   public void updateWither()
   {}

   @Override
   public void witherDied()
   {
      ownerWither.worldObj.newExplosion(ownerWither, ownerWither.posX, ownerWither.posY + (double)ownerWither.getEyeHeight(), ownerWither.posZ, 7.0F * knellStrengthMultiplier, false, ownerWither.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
   }

	@Override
	public boolean increasePower() 
	{
	   if(super.increasePower())
	   {
	      knellStrengthMultiplier *= 1.5f;
         return true;
	   }
	   return false;
	}
};
