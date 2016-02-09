package thor12022.hardcorewither.items;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class AbstractItem extends Item implements IItem
{

   // If you aren't setting multiple textures for your item. IE: Non-Metadata
   // items.
   public AbstractItem()
   {
      super();

      setUnlocalizedName(ModInformation.ID + "." + name());
      setCreativeTab(HardcoreWither.tabBaseMod);
   }
   
   @Override
   public void registerItem()
   {
      GameRegistry.registerItem(this, name());
   }

   @Override
   public void registerModel()
   {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInformation.ID + ":" + name()));
   }
}
