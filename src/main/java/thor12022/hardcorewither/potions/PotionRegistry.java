package thor12022.hardcorewither.potions;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.HardcoreWither;
import net.minecraft.potion.Potion;

@Configurable
public class PotionRegistry
{
   
   public static Potion potionAntiWither;

   public PotionRegistry()
   {}
   
   //! @todo I'd prefer to do construction at construction. . .
   public void registerPotions()
   {
      potionAntiWither = new PotionAntiWither().setPotionName("potion.antiWither");
   }
}
