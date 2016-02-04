package thor12022.hardcorewither.potions;

import net.minecraft.potion.Potion;

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
