package thor12022.hardcorewither.powerUps;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thor12022.hardcorewither.HardcoreWither;

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
            HardcoreWither.logger.warn(excp.getMessage());
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
            HardcoreWither.logger.warn(excp.getMessage());
         }
      }
   }
   
   protected PowerUpDamageResistance()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
   }
   
   private PowerUpDamageResistance(EntityWither theOwnerWither)
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
      return new PowerUpDamageResistance(theOwnerWither);
   }

   @Override
   public boolean increasePower() 
   {
      if(super.increasePower())
      {

         PotionEffect resistanceEffect = new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, powerStrength);
         try
         {
            Map<Integer, PotionEffect> activePotionsMap = (Map<Integer, PotionEffect>) activePotionMapField.get(ownerWither); 
            if (activePotionsMap.containsKey(Integer.valueOf(resistanceEffect.getPotionID())))
            {
               activePotionsMap.remove(Integer.valueOf(resistanceEffect.getPotionID()));
            }
            activePotionsMap.put(Integer.valueOf(resistanceEffect.getPotionID()), resistanceEffect);
             
            onNewPotionEffectMethod.invoke(ownerWither, resistanceEffect);
         }
         catch(Exception e)
         {
            HardcoreWither.logger.warn(e.getMessage());
            return false;
         }
         return true;
      }
      return false;
   }
}