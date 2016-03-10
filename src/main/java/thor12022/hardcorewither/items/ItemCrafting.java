package thor12022.hardcorewither.items;

import java.util.List;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thor12022.hardcorewither.ModInformation;

public class ItemCrafting extends AbstractItem
{
   private final static String NAME = "craftingItem";
      
   private final static int META_STARRY_EMERALD = 0;
   private final static int META_STARRY_STICK = 1;
   
   private final static String NAMES[] = 
      {
            "starryEmerald",
            "starryStick"
      };
   
   public ItemCrafting()
   {
      super();
      this.setHasSubtypes(true);
   }
   
   @Override
   public String getUnlocalizedName(ItemStack stack)
   {
      if(stack.getMetadata() < NAMES.length)
      {
         return "item." + ModInformation.ID + "." + NAMES[stack.getMetadata()];
      }
      return null;
   }
   
   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs creativeTab, List itemStacks)
   {
      for(int i = 0; i < NAMES.length; ++i)
      {
         itemStacks.add(new ItemStack(item, 1, i));
      }
   }
   
   
   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack)
   {
       return stack.getMetadata() == 0 || stack.getMetadata() == 1;
   }

   @Override
   public String name()
   {
      return NAME;
   }

   @Override
   public void registerModel()
   {
      for(int i = 0; i< NAMES.length; ++i)
      {
         ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(ModInformation.ID + ":" + NAMES[i]));
      }
   }
   
   @Override
   public void registerRecipe()
   {
      GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(this,1,META_STARRY_EMERALD), Items.nether_star, "gemEmerald"));
      GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(this,4,META_STARRY_STICK), Items.nether_star, "stickWood", "stickWood", "stickWood", "stickWood"));
   }
   
   @Override
   public boolean isEnabled()
   {
      return true;
   }

}
