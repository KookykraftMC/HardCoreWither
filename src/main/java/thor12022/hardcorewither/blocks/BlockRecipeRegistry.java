package thor12022.hardcorewither.blocks;

/*
 * General place to do all your block related recipe things'n'stuff.
 */

public class BlockRecipeRegistry
{

   // Self explanatory. Continue these how you wish. EG:
   // registerPulverizerRecipes
   private static void registerShapedRecipes()
   {
      // GameRegistry.addRecipe(new ShapedOreRecipe(new
      // ItemStack(Blocks.gold_ore), new Object[]{"XXX", "X X", "XXX", 'X',
      // "ingotGold"}));
   }

   private static void registerShaplessRecipes()
   {

   }

   public static void registerBlockRecipes()
   {
      registerShapedRecipes();
      registerShaplessRecipes();
   }
}
