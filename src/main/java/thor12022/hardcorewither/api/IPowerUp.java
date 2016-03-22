package thor12022.hardcorewither.api;

import net.minecraft.entity.boss.EntityWither;

public interface IPowerUp
{   
   /**
    * Power Up action to be performed on tick
    * @param data value returned from applyPowerUp
    */
   void updateWither(EntityWither wither, int strength, IPowerUpStateData data);
   
   /**
    * Power Up action to perform when the Wither dies
    * @param data value returned from applyPowerUp 
    */
   void witherDied(EntityWither wither, int strength, IPowerUpStateData data);
      
   /**
    * Initializes the Power Up
    * @param wither the affected Wither
    * @param strength the strength of the Power Up
    * @post this will apply the PowerUp regardless of min Wither Level and Max Strength
    * @return an object representing state data for this Power Up, actual type needs only be know by PowerUp itself
    * @note return value should contain all necessary info, including wither and strength data
    */
   IPowerUpStateData applyPowerUp(EntityWither wither, int strength);
   
   /**
    * Allows the PowerUp to not be applicable to a wither for any arbitrary reason
    * @param wither
    * @return false if this PowerUp should not apply to the supplied wither
    * @note this should be independent from the minWitherLevel and maxStrength methods, 
    *    e.g. will only work with a player nearby, or will only work in certain dimensions
    */
   boolean canApply(EntityWither wither);
   
   /**
    * Minimum level the Wither must be before this Power Up can be used
    */
   int minWitherLevel();
   
   /**
    * Maximum strength this Power Up will support
    */
   int maxStrength();

   /**
    * Name of Power Up, used for NBT, Commands, etc. 
    * @note not a display name, must not contain symbols or spaces
    */
   String getName();
}
