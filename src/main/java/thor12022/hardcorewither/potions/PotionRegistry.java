package thor12022.hardcorewither.potions;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.HardcoreWither;
import net.minecraft.potion.Potion;

@Configurable
public class PotionRegistry
{
   
   public static Potion potionAntiWither;

   @Config(minInt = 24, comment = "Ensure this does not get set higher than the size of the potion array")
   private static int antiWitherPotionId = 30;

   public PotionRegistry()
   {
      ConfigManager.getInstance().register(this);
   }
   
   public void registerPotions()
   {
      if(antiWitherPotionId == -1)
      {
         antiWitherPotionId = NextPotionId();
      }
      
      HardcoreWither.logger.debug("Assigning Potion ID " + antiWitherPotionId + " to Anti-Wither");
      potionAntiWither = new PotionAntiWither(antiWitherPotionId).setPotionName("potion.antiWither");
   }

   private static int NextPotionId()
   {
      for( int potionId = 34; potionId < Potion.potionTypes.length; potionId++ )
      {
         if( Potion.potionTypes[potionId] == null )
         {
            return potionId;
         }
      }
      return -1;
   }
}
