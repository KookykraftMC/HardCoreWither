package thor12022.hardcorewither.wither.powerups;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;

@Configurable
public class PowerUpHealthBoost extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 128;
   private final static int DEFAULT_MIN_LEVEL = 1;

   @Config(minFloat = 1f, maxFloat = 10f)
   private static float healthBoostMultiplier = 1.1f;

   public PowerUpHealthBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      if(wither.isServerWorld())
      {
         if(wither.getInvulTime() >= 20)
         {
            float baseHealth = (float) wither.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
            // ! @todo this is close, but not quite right, it is a bit too fast
            // at higher levels
            wither.heal((((baseHealth * (2f / 3f)) - 200) / 200));
         }
      }
   }

   @Override
   public void witherDied(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public IPowerUpStateData applyPowerUp(EntityWither wither, int strength)
   {      
      double health = wither.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
      double newHealth = health * healthBoostMultiplier;
      wither.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newHealth);
      return null;
   }
}
