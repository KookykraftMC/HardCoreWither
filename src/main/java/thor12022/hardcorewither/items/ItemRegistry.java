package thor12022.hardcorewither.items;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thor12022.hardcorewither.HardcoreWither;

public class ItemRegistry
{
   public static Item starryApple         = new ItemStarryApple();
   public static Item starryAxe           = new ItemStarryAxe();
   public static Item starryPickaxe       = new ItemStarryPickaxe();
   public static Item starryShovel        = new ItemStarryShovel();
   public static Item starrySword         = new ItemStarrySword();
   public static Item craftingItem        = new ItemCrafting();

   private ArrayList<IItem> items = new ArrayList();
   
   public ItemRegistry()
   {
      // Collect all the declared items into a list for easy registering
      for( Field field : getClass().getFields())
      {
         if(field.getType() == Item.class)
         {
            try
            {
               //Checks if it is castable to IItem and static
               items.add((IItem)field.get(null));
            }
            catch(Exception e)
            {
               HardcoreWither.logger.debug("Item not properly defined in registry: " + field.getName());
            }
         }
      }
   }
   
   public void registerItems()
   {
      for(IItem item : items)
      {
         if(item.isEnabled())
         {
            item.registerItem();
         }
      }
   }
   
   @SideOnly(Side.CLIENT)
   public void registerModels()
   {
      for(IItem item : items)
      {
         if(item.isEnabled())
         {
            item.registerModel();
         }
      }
   }
   
   public void registerRecipes()
   {
      for(IItem item : items)
      {
         if(item.isEnabled())
         {
            item.registerRecipe();
         }
      }
   }
}
