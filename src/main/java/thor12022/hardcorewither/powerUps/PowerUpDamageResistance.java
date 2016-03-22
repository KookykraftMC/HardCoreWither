package thor12022.hardcorewither.powerUps;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Configurable;

@Configurable // This class has no @Config members, but it parent class does
public class PowerUpDamageResistance extends AbstractPowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 3;   
   private final static int DEFAULT_MIN_LEVEL = 1;
   
   private static Method onNewPotionEffectMethod;
   private static Field  activePotionMapField;
   
   static
   {
      // Try to get the obfuscated names first, then fall back on the deobfuscated
      try
      {
         onNewPotionEffectMethod = EntityLivingBase.class.getDeclaredMethod("func_70670_a", PotionEffect.class);
         onNewPotionEffectMethod.setAccessible(true);
      }
      catch(Exception e)
      {
         try
         {
            onNewPotionEffectMethod = EntityLivingBase.class.getDeclaredMethod("onNewPotionEffect", PotionEffect.class);
            onNewPotionEffectMethod.setAccessible(true);
         }
         catch(Exception excp)
         {
            HardcoreWither.LOGGER.warn(excp.getMessage());
         }
      }
      try
      {
         activePotionMapField = EntityLivingBase.class.getDeclaredField("field_70713_bf");
         activePotionMapField.setAccessible(true);
      }
      catch(Exception e)
      {
         try
         {
            activePotionMapField = EntityLivingBase.class.getDeclaredField("activePotionsMap");
            activePotionMapField.setAccessible(true);
         }
         catch(Exception excp)
         {
            HardcoreWither.LOGGER.warn(excp.getMessage());
         }
      }
   }
   
   protected PowerUpDamageResistance()
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
      PotionEffect resistanceEffect = new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, strength);
      try
      {
         Map<Integer, PotionEffect> activePotionsMap = (Map<Integer, PotionEffect>) activePotionMapField.get(wither); 
         if (activePotionsMap.containsKey(Integer.valueOf(resistanceEffect.getPotionID())))
         {
            activePotionsMap.remove(Integer.valueOf(resistanceEffect.getPotionID()));
         }
         activePotionsMap.put(Integer.valueOf(resistanceEffect.getPotionID()), resistanceEffect);
          
         onNewPotionEffectMethod.invoke(wither, resistanceEffect);
      }
      catch(Exception e)
      {
         HardcoreWither.LOGGER.warn(e.getMessage());
      }
      return null;
   }
}
