package thor12022.hardcorewither.items;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.potions.PotionRegistry;
import thor12022.hardcorewither.util.TextHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemStarryApple extends ItemFood implements IItem
{
   private final static String NAME = "starryApple";
      
   public ItemStarryApple()
   {
      super(4, 1.2F, false);
      setUnlocalizedName(ModInformation.ID + "." + NAME);
      setCreativeTab(HardcoreWither.tabBaseMod);
      this.setAlwaysEdible();
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
      list.add(TextHelper.GREEN + TextHelper.ITALIC + TextHelper.localize("tooltip." + ModInformation.ID + ".starryApple"));
   }
   
   @Override
   public EnumRarity getRarity(ItemStack stack)
   {
       return EnumRarity.EPIC;
   }

   @Override
   public void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
   {
       if (!world.isRemote)
       {
          player.addPotionEffect(new PotionEffect(PotionRegistry.potionAntiWither.id, 2400, 0));
          player.addPotionEffect(new PotionEffect(Potion.absorption.id,             1200, 0));
          player.addPotionEffect(new PotionEffect(Potion.regeneration.id,           900, 1));
          player.addPotionEffect(new PotionEffect(Potion.resistance.id,             900, 1));
          player.addPotionEffect(new PotionEffect(Potion.damageBoost.id,            900, 1));
          player.addPotionEffect(new PotionEffect(Potion.heal.id,                   0,    0));
       }
   }

   @Override
   public String name()
   {
      return NAME;
   }

   @Override
   public void registerItem()
   {
      GameRegistry.registerItem(this, NAME);
   }

   @Override
   public void registerModel()
   {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInformation.ID + ":" + NAME));
   }

   @Override
   public void registerRecipe()
   {
      GameRegistry.addShapedRecipe(new ItemStack(this), new Object[]{
            " s ", 
            "sas", 
            " s ", 
            'a', Items.apple, 
            's', Items.nether_star});
   }
}
