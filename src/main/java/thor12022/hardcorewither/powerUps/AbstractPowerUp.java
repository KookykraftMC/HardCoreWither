package thor12022.hardcorewither.powerUps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.IConfigClass;
import thor12022.hardcorewither.interfaces.INBTStorageClass;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

abstract class AbstractPowerUp implements IPowerUp, IConfigClass
{
   final protected EntityWither ownerWither;
   final protected String className = getClass().getSimpleName();
   protected int powerStrength;
   protected boolean powerUpEnabled = true;
   private int maxStrength = 20;
   private int minLevel = 1;
   
   static private Set<String> constructedPrototypeClasses = new HashSet<String>();
      
   AbstractPowerUp( int minLevel, int maxStrength)
   {
      ownerWither = null;
      powerStrength = 0;
      this.maxStrength = maxStrength;
      this.minLevel = minLevel;
      if(constructedPrototypeClasses.contains(className))
      {
         HardcoreWither.logger.debug("Duplicate Prototype constructed for " + className);
      }
      else
      {
         constructedPrototypeClasses.add(className);
         ConfigManager.getInstance().addConfigClass(this);
      }
   }
   
   protected AbstractPowerUp(EntityWither theOwnerWither)
   {
      powerStrength = 1;
      ownerWither = theOwnerWither;
   }
   
   @Override
   public void writeToNBT(NBTTagCompound nbt)
   {
      nbt.setInteger("powerStrength", powerStrength);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt)
   {
      powerStrength = nbt.getInteger("powerStrength");
      powerStrength = powerStrength <= 0 ? 1 : powerStrength;
   }
   
   @Override
   public boolean increasePower()
   {
      if(powerStrength < maxStrength)
      {
         ++powerStrength;
         return true;
      }
      return false;
   }
   
   @Override
   public String getName()
   {
      return className;
   }
   
   @Override
   public int minPower()
   {
      return powerUpEnabled ? minLevel : Integer.MAX_VALUE;
   }
   
   @Override
   public void syncConfig(Configuration config)
   {
      powerUpEnabled = config.getBoolean("Enabled", this.getSectionName(), powerUpEnabled, "");
      minLevel = config.getInt("minLevel", this.getSectionName(), minLevel, 0, Integer.MAX_VALUE, "The Minimum Wither Level for which this Power Up is available");
      maxStrength = config.getInt("maxStrength", this.getSectionName(), maxStrength, 0, Integer.MAX_VALUE, "The Maximum Strength this Power Up can reach");
   }
}
