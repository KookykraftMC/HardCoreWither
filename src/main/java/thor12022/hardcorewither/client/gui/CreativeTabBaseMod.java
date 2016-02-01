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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabBaseMod extends CreativeTabs
{

   private ItemStack icon = new ItemStack( Items.skull, 1, 1);;
   
   public CreativeTabBaseMod(String tabLabel)
   {
      super(tabLabel);
      setBackgroundImageName(ModInformation.ID + ".png");
   }

   public boolean hasSearchBar()
   {
      return false;
   }
   
   // The tab icon is what you return here.
   @SideOnly(Side.CLIENT)
   @Override
   public ItemStack getIconItemStack()
   {
      return icon;
   }


   @SideOnly(Side.CLIENT)
   @Override
   public int getIconItemDamage() {
     return icon.getItemDamage();
   }
   
   @SideOnly(Side.CLIENT)
   @Override
   public Item getTabIconItem()
   {
      return icon.getItem();
   }
}
