package thor12022.hardcorewither.powerUps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.interfaces.INBTStorageClass;
import thor12022.hardcorewither.util.MultiRange;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

@Configurable (syncNotification = "configChangeNotification")
abstract class AbstractPowerUp implements IPowerUp
{
   final protected EntityWither ownerWither;
   final protected String className = getClass().getSimpleName();
   protected int powerStrength;
   
   @Config
   protected boolean powerUpEnabled = true;
   
   @Config(minInt = 2, comment = "The Maximum Strength this Power Up can reach")
   private int maxStrength = 20;
   
   @Config(minInt = 1, comment = "The Minimum Wither Level for which this Power Up is available")
   private int minLevel = 1;
   
   @Config(comment="Dimensions where this Power Up will not be used, e.g. \"-1,0,1-100000\"")
   private String blacklistDims = "";
   private MultiRange blacklistDimsRanges;
   
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
      }
   }
   
   protected AbstractPowerUp(EntityWither theOwnerWither)
   {
      powerStrength = 1;
      ownerWither = theOwnerWither;
   }
   
   protected boolean isValidApplication(EntityWither theOwnerWither)
   {
      return ((blacklistDimsRanges != null) && !blacklistDimsRanges.contains(theOwnerWither.dimension));
   }
   
   private void configChangeNotification()
   {
      blacklistDimsRanges = new MultiRange(blacklistDims);
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

   /**
    * Most (all?) powerups will not persist when the world is unloaded anyway
    */
   @Override
   public void resetNBT()
   {}
}
