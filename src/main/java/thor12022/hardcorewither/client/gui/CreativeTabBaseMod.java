package thor12022.hardcorewither.client.gui;

//Creates your creative tab.

import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.items.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;

public class CreativeTabBaseMod extends CreativeTabs
{

   public CreativeTabBaseMod(String tabLabel)
   {
      super(tabLabel);
      setBackgroundImageName(ModInformation.ID + ".png"); // Automagically has
                                                          // tab_ applied to it.
                                                          // Make sure you
                                                          // change the texture
                                                          // name.
   }

   public boolean hasSearchBar()
   {
      return false;
   }

   // The tab icon is what you return here.
   @Override
   public ItemStack getIconItemStack()
   {
      return new ItemStack( Items.skull, 0, 1);
   }

   @Override
   public Item getTabIconItem()
   {
      return new Item();
   }
}
