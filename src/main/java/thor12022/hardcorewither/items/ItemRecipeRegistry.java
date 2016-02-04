package thor12022.hardcorewither.items;

/*
 * General place to do all your item related recipe things'n'stuff.
 */

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRecipeRegistry
{
   // Self explanatory. Continue these how you wish. EG: registerSmeltingRecipes
   private static void registerShapedRecipes()
   {
      // GameRegistry.addRecipe(new ShapedOreRecipe(new
      // ItemStack(Items.blaze_rod), new Object[]{"X  ", " X ", "  X", 'X',
      // "powderBlaze"}));  
     
      if(ItemRegistry.starryApple != null)
      {
         GameRegistry.addShapedRecipe(new ItemStack(ItemRegistry.starryApple), new Object[]{
            " s ", 
            "sas", 
            " s ", 
            'a', Items.apple, 
            's', Items.nether_star});
      }
      
   }

   private static void registerShaplessRecipes()
   {
      // GameRegistry.addShapelessRecipe(new
      // ItemStack(BlockRegistry.exampleBlock), new
      // ItemStack(ItemRegistry.exampleItem, 9));
   }

   public static void registerItemRecipes()
   {
      registerShapedRecipes();
      registerShaplessRecipes();
   }
}
