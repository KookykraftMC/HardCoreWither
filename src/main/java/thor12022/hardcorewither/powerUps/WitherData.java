package thor12022.hardcorewither.powerUps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.api.IPowerUp;
import thor12022.hardcorewither.api.InvalidPowerUpException;

class WitherData implements IExtendedEntityProperties
{
   private static final String NBT_TAG = ModInformation.CHANNEL;
      
   private final Map<String, PowerUpEffect> activePowerUps       =  new HashMap<String, PowerUpEffect>();
   private boolean                           hasDied              =  false;
   private boolean                           isPendingLootDrops   =  false;
   private boolean                           isPoweredUp          =  false;
   private EntityWither                      ownerWither;
   
   static boolean register(EntityWither wither)
   {
      if(wither.getExtendedProperties(NBT_TAG) == null)
      {
         wither.registerExtendedProperties(NBT_TAG, new WitherData());
         return true;
      }
      return false;
   }
   
   static WitherData getWitherData(EntityWither wither)
   {
      return (WitherData) wither.getExtendedProperties(NBT_TAG);
   }
   
   /**
   * @returns number of PowerUpEffects active on this Wither
   */
  Collection<PowerUpEffect> getActivePowerUpEffects()
  {
     return activePowerUps.values();
  }
   
   /**
    * @param powerUp
    * @returns null if PowerUp not active
    * @todo having this allows some other class to change the strength
    */
   PowerUpEffect getActivePowerUpEffect(IPowerUp powerUp)
   {
      return activePowerUps.get(powerUp.getName());
   }
   
   /**
    * @returns true if successful, false if PowerUpEffect already exists (or other error)
    */
   boolean addPowerUpEffect(String powerUpName, int powerUpStrength)
   {
      if(activePowerUps.get(powerUpName) == null)
      {
         try
         {
            activePowerUps.put(powerUpName, new PowerUpEffect(ownerWither, powerUpName, powerUpStrength));
         }
         catch(InvalidPowerUpException e)
         {
            return false;
         }
         return true;
      }
      return false;         
   }
   
   /**
    * @param 
    * @returns true if successful, false if PowerUpEffect didn't exist
    */
   boolean removePowerUpEffect(String powerUpName)
   {
      return activePowerUps.put(powerUpName, null) != null;
   }
   
   boolean isPendingLootDrops()
   {
      return isPendingLootDrops;
   }

   void setPendingLootDrops(boolean isPendingLootDrops)
   {
      this.isPendingLootDrops = isPendingLootDrops;
   }

   /**
    * @returns the total strength of all PowerUps
    * @note recalculated every call
    * @todo cache this?
    */
   int getStrength()
   {
      int strength = 0;
      for(PowerUpEffect powerUpEffect : activePowerUps.values())
      {
         if(powerUpEffect != null)
         {
            strength += powerUpEffect.getStrength();
         }
      }
      return strength;
   }

   public boolean isPoweredUp()
   {
      return isPoweredUp;
   }

   public void setPoweredUp(boolean isPoweredUp)
   {
      this.isPoweredUp = isPoweredUp;
   }

   public EntityWither getWither()
   {
      return ownerWither;
   }

   /**
    * tick update
    */
   void update()
   {
      for(PowerUpEffect powerUp : activePowerUps.values())
      {
         try
         {
            powerUp.updateEffect();
         }
         // If any exception occurred, assume it's this powerup's fault
         catch(Exception e)
         {            
            HardcoreWither.LOGGER.error(e);
            HardcoreWither.LOGGER.error("Error Occured while updating PowerUp " + powerUp.getName() + ", removing.");
            activePowerUps.remove(powerUp);
         }
      }
   }
   
   /**
    * died update
    */
   void died()
   {
      for(PowerUpEffect powerUp : activePowerUps.values())
      {
         powerUp.onDeathEffect();
      }
   }
      
   @Override
   public void saveNBTData(NBTTagCompound compound)
   {
      NBTTagCompound witherDataTag = new NBTTagCompound();
      compound.setTag(NBT_TAG, witherDataTag);
      
      NBTTagList powerUpEffectsTagList = new NBTTagList();
      
      for(PowerUpEffect effect : activePowerUps.values())
      {
         powerUpEffectsTagList.appendTag(effect.serializeNBT());
      }
      witherDataTag.setTag("powerUpEffectsList", powerUpEffectsTagList);
      witherDataTag.setBoolean("hasDied", hasDied);
      witherDataTag.setBoolean("isPendingLootDrops", isPendingLootDrops);
   }
   
   @Override
   public void loadNBTData(NBTTagCompound compound)
   {
      NBTTagCompound witherDataTag = compound.getCompoundTag(NBT_TAG);
      NBTTagList powerUpEffectsTagList = witherDataTag.getTagList("powerUpEffectsList", (new NBTTagCompound()).getId());
      for(int i = 0; i < powerUpEffectsTagList.tagCount(); ++i)
      {
         try
         {
            PowerUpEffect effect = PowerUpEffect.createFromNbt(ownerWither, powerUpEffectsTagList.getCompoundTagAt(i));
            activePowerUps.put(effect.getName(), effect);
         }
         catch(InvalidPowerUpException e)
         {
            HardcoreWither.LOGGER.warn(e);
            HardcoreWither.LOGGER.warn("Problem loading " + powerUpEffectsTagList.getCompoundTagAt(i).getId() + " NBT for EntityWither, " + ownerWither.getUniqueID());
         }
      }
   }
   
   @Override
   public void init(Entity entity, World world)
   {
      ownerWither = (EntityWither) entity;
   }   
}

