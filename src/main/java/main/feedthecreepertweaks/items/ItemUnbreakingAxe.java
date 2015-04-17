package main.feedthecreepertweaks.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import main.feedthecreepertweaks.FeedTheCreeperTweaks;
import main.feedthecreepertweaks.ModInformation;
import main.feedthecreepertweaks.util.TextHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemUnbreakingAxe extends ItemAxe
{
   public ItemUnbreakingAxe(ToolMaterial toolMaterial, String name, String texture)
   {
      super(toolMaterial);
      this.setTextureName(texture);
      this.setUnlocalizedName(ModInformation.ID + "." + name);
      this.setCreativeTab(FeedTheCreeperTweaks.tabBaseMod);
   }
   
   @Override
   @SideOnly(Side.CLIENT)
   public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
   {
      list.add(TextHelper.GRAY + TextHelper.ITALIC + TextHelper.localize("tooltip." + ModInformation.ID + ".unbreaking"));
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
      return stack.getItem() instanceof ItemUnbreakingAxe;
   }
}
