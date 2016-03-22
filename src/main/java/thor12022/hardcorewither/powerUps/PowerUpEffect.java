package thor12022.hardcorewither.powerUps;

import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.api.InvalidPowerUpException;
import thor12022.hardcorewither.api.PowerUpRegistry;

class PowerUpEffect
{
   private String powerUpName;
   private IPowerUpStateData powerUpData;
   private final EntityWither ownerWither;
   private int strength;
   
   static PowerUpEffect createFromNbt(EntityWither ownerWither, NBTTagCompound tag) throws InvalidPowerUpException
   {
      String name = tag.getString("powerUpId");
      int strength = tag.getInteger("strength");
      PowerUpEffect effect = new PowerUpEffect(ownerWither, name, strength);
      effect.powerUpData = PowerUpRegistry.get(name).applyPowerUp(ownerWither, strength);
      if(effect.powerUpData != null)
      {
         effect.powerUpData.deserializeNBT(tag.getCompoundTag("stateData"));
      }
      return effect;
   }
   
   PowerUpEffect(EntityWither wither, String powerUpName, int strength) throws InvalidPowerUpException
   {
      ownerWither = wither;
      this.powerUpName = powerUpName;
      this.strength = strength;
      powerUpData = PowerUpRegistry.get(powerUpName).applyPowerUp(wither, strength);
   }
   
   PowerUpEffect(EntityWither wither)
   {
      ownerWither = wither;
   }
   
   /**
    * @returns the new Power Up Effect Strength
    * @note will not result in an effect strength less than 0,
    *    or greater than that allowed by the Power Up
    * @todo currently replaces existing with a new one, should actually affect it
    */
   int adjustEffect(int amount)
   {
      strength += amount;
      if(strength <= 0)
      {
         powerUpData = null;
         strength = 0;
      }
      else
      {
         try
         {
            powerUpData = PowerUpRegistry.get(powerUpName).applyPowerUp(ownerWither, strength);
         }
         catch(InvalidPowerUpException e)
         {
            HardcoreWither.logger.error("PowerUpRegistry has been modified after initialization! " + powerUpName + " no longer valid");
            powerUpData = null;
            strength = 0;
         }
      }
      return strength;
   }
   
   void updateEffect()
   {
      try
      {
         PowerUpRegistry.get(powerUpName).updateWither(ownerWither, strength, powerUpData);
      }
      catch(InvalidPowerUpException e)
      {
         HardcoreWither.logger.error("PowerUpRegistry has been modified after initialization! " + powerUpName + " no longer valid");
         powerUpData = null;
         strength = 0;
      }
   }
   
   void onDeathEffect()
   {
      try
      {
         PowerUpRegistry.get(powerUpName).witherDied(ownerWither, strength, powerUpData);
      }
      catch(InvalidPowerUpException e)
      {
         HardcoreWither.logger.error("PowerUpRegistry has been modified after initialization! " + powerUpName + " no longer valid");
         powerUpData = null;
         strength = 0;
      }
   }
   
   String getName()
   {
      return powerUpName;
   }
   
   int getStrength()
   {
      return strength;
   }

   public NBTTagCompound serializeNBT()
   {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("powerUpId", powerUpName);
      tag.setInteger("strength", strength);
      if(powerUpData != null)
      {
         NBTTagCompound stateDataTag = powerUpData.serializeNBT();
         tag.setTag("powerUpData", stateDataTag);
      }
      return tag;
   }
}
