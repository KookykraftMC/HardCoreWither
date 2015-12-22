package thor12022.hardcorewither.items;

/*
 * Class to register your blocks in.
 * The order that you list them here is the order they are registered.
 * Keep that in mind if you like nicely organized creative tabs.
 */

import cpw.mods.fml.common.registry.GameRegistry;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.ModInformation;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

@Configurable
public class ItemRegistry
{
   // public static Item exampleItem;
   public static Item starryApple;

   @Config
   private boolean enableStarryApple = true;

   public ItemRegistry()
   {
      ConfigManager.getInstance().register(this);
   }
   
   public void registerItems()
   {
      starryApple = new ItemStarryApple();
      if(enableStarryApple)
      {
         GameRegistry.registerItem(starryApple, "StarryApple");
      }
   }
}
