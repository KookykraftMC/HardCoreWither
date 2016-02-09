package thor12022.hardcorewither.items;

import java.util.List;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.util.TextHelper;

public class ItemStarryShovel extends ItemSpade implements IItem
{   
   @Config
   private boolean isEnabled = true;
   
   private static final String NAME = "starryShovel";
   
   public ItemStarryShovel()
   {
      super(MaterialHelper.witherEmerald);
      this.setUnlocalizedName(ModInformation.ID + "." + NAME);
      this.setCreativeTab(HardcoreWither.tabBaseMod);
   }
   
   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack)
   {
       return true;
   }
   
   @Override
   @SideOnly(Side.CLIENT)
   public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
   {
      list.add(TextHelper.GRAY + TextHelper.ITALIC + TextHelper.localize("tooltip." + ModInformation.ID + ".unbreaking" ));
   }
   
   @Override
   public int getMaxDamage()
   {
       return 0;
   }  
   
   @Override
   public boolean isDamageable()
   {
      return false;
   }
   
   @Override
   public boolean isItemTool(ItemStack stack)
   {
      return stack.getItem() instanceof ItemStarryShovel;
   }

   @Override
   public String name()
   {
      return NAME;
   }

   @Override
   public void registerItem()
   {
      if(isEnabled)
      {
         GameRegistry.registerItem(this, NAME);
      }
   }

   @Override
   public void registerModel()
   {
      if(isEnabled)
      {
         ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInformation.ID + ":" + NAME));
      }
   }
   
   @Override
   public void registerRecipe()
   {
      if(isEnabled)
      {
         
      }
   }
}
