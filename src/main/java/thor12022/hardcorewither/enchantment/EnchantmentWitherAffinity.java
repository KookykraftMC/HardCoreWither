package thor12022.hardcorewither.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;

@Configurable
public class EnchantmentWitherAffinity extends Enchantment
{
   static final String NAME = "witherAffinity";
   
   @Config(maxInt=255, minInt = 63)
   private static int enchantId = 97;
   
   static
   {
      HardcoreWither.CONFIG.register(EnchantmentWitherAffinity.class);
   }
    
   EnchantmentWitherAffinity()
   {
      super(enchantId, new ResourceLocation(ModInformation.ID + "." + NAME), 0, EnumEnchantmentType.WEAPON);
      this.setName(ModInformation.ID + "." + NAME);
   }
   
   @Override
   public boolean canApply(ItemStack stack)
   {
      return false;
   }

   @Override
   public boolean canApplyTogether(Enchantment enchant)
   {
      return false;
   }
   
   @Override
   public boolean isAllowedOnBooks()
   {
       return false;
   }
}