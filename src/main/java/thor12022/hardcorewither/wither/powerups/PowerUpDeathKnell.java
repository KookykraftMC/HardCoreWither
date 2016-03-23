package thor12022.hardcorewither.wither.powerups;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.boss.EntityWither;

@Configurable
class PowerUpDeathKnell extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 10;
   private final static int DEFAULT_MIN_LEVEL = 1;
   
   @Config(minFloat = 0f, maxFloat = 10f)
   private static float knellStrengthMultiplier = 0.6666667f;
   
   protected PowerUpDeathKnell()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.CONFIG.register(this);   
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public void witherDied(EntityWither wither, int strength, IPowerUpStateData data)
   {
      wither.worldObj.newExplosion(wither, wither.posX, wither.posY + wither.getEyeHeight(), wither.posZ, 7.0F * knellStrengthMultiplier * strength, false, wither.worldObj.getGameRules().getBoolean("mobGriefing"));
   }

   @Override
   public IPowerUpStateData applyPowerUp(EntityWither wither, int strength)
   {
      return null;
   }
}
