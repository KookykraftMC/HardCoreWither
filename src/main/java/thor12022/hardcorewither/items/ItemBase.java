package thor12022.hardcorewither.items;

/*
 * Base item class for getting standard things done with quickly.
 * Extend this for pretty much every item you make.
 */

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import net.minecraft.item.Item;

public class ItemBase extends Item
{

   // If you aren't setting multiple textures for your item. IE: Non-Metadata
   // items.
   public ItemBase(String unlocName)
   {
      super();

      setUnlocalizedName(ModInformation.ID + "." + unlocName);
      setCreativeTab(HardcoreWither.tabBaseMod);
   }
}
