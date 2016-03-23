package thor12022.hardcorewither.wither.powerups;

import thor12022.hardcorewither.api.IPowerUp;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.util.MultiRange;
import net.minecraft.entity.boss.EntityWither;

@Configurable (syncNotification = "configChangeNotification")
abstract class AbstractPowerUp implements IPowerUp
{   
   @Config
   protected boolean powerUpEnabled = true;
   
   @Config(minInt = 2, comment = "The Maximum Strength this Power Up can reach")
   private int maxStrength = 20;
   
   @Config(minInt = 1, comment = "The Minimum Wither Level for which this Power Up is available")
   private int minLevel = 1;
   
   @Config(comment="Dimensions where this Power Up will not be used, e.g. \"-1,0,1-100000\"")
   private String blacklistDims = "";
   private MultiRange blacklistDimsRanges;
   
   protected AbstractPowerUp(int minLevel, int maxStrength)
   {
      this.maxStrength = maxStrength;
      this.minLevel = minLevel;
   }
   
   @Override
   public boolean canApply(EntityWither theOwnerWither)
   {
      return powerUpEnabled && 
             ( (blacklistDimsRanges != null) && 
               !blacklistDimsRanges.contains(theOwnerWither.dimension));
   }
   
   @SuppressWarnings("unused")
   private void configChangeNotification()
   {
      blacklistDimsRanges = new MultiRange(blacklistDims);
   }

   @Override
   public int maxStrength()
   {
      return maxStrength;
   }
   
   @Override
   public String getName()
   {
      return getClass().getSimpleName();
   }
   
   @Override
   public int minWitherLevel()
   {
      return powerUpEnabled ? minLevel : Integer.MAX_VALUE;
   }
}
