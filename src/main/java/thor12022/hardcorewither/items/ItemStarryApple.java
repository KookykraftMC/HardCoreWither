package thor12022.hardcorewither.items;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.potions.PotionRegistry;
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

@Configurable
public class ItemStarryApple extends ItemFood implements IItem
{
   @Config(minInt = 0)
   private static int antiWitherDurationTicks = 2400;
   
   @Config(minInt = 0)
   private static int absorptionDurationTicks = 1200;
   
   @Config(minInt = 0)
   private static int regenDurationTicks = 900;
   
   @Config(minInt = 0)
   private static int resistanceDurationTicks = 900;
   
   @Config(minInt = 0)
   private static int strengthDurationTicks = 900;
   
   @Config(minInt = 0)
   private static int antiWitherLevel = 1;
   
   @Config(minInt = 0)
   private static int absorptionLevel = 1;
   
   @Config(minInt = 0)
   private static int regenLevel = 2;
   
   @Config(minInt = 0)
   private static int resistanceLevel = 2;
   
   @Config(minInt = 0)
   private static int strengthLevel = 2;
   
   @Config(minInt = 0)
   private static int instaHealLevel = 1;
   
   @Config
   private static boolean enableStarryApple = true;
   
   static
   {
      HardcoreWither.config.register(ItemStarryApple.class);
   }
   
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
   public EnumRarity getRarity(ItemStack stack)
   {
       return EnumRarity.EPIC;
   }

   @Override
   public void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
   {
       if (!world.isRemote)
       {
          player.addPotionEffect(new PotionEffect(PotionRegistry.potionAntiWither.id,  antiWitherDurationTicks,   antiWitherLevel - 1));
          player.addPotionEffect(new PotionEffect(Potion.absorption.id,                absorptionDurationTicks,   absorptionLevel - 1));
          player.addPotionEffect(new PotionEffect(Potion.regeneration.id,              regenDurationTicks,        regenLevel - 1));
          player.addPotionEffect(new PotionEffect(Potion.resistance.id,                resistanceDurationTicks,   resistanceLevel - 1));
          player.addPotionEffect(new PotionEffect(Potion.damageBoost.id,               strengthDurationTicks,     strengthLevel - 1));
          player.addPotionEffect(new PotionEffect(Potion.heal.id,                      0,                         instaHealLevel - 1));
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

   @Override
   public boolean isEnabled()
   {
      return enableStarryApple;
   }
}
