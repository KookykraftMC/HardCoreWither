package thor12022.hardcorewither.items;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;

@Configurable
public class MaterialHelper
{
   @Config(minInt=0)
   private static int witheredEmeraldHarvestLevel = 4;
   
   @Config(minInt=0)
   private static int witheredEmeraldMaxUses = 0;
   
   @Config(minFloat=0f)
   private static float witheredEmeraldEfficiency = 16.0F;
   
   @Config(minFloat=0f)
   private static float witheredEmeraldAttackDamage = 10.5F;

   @Config(minInt=0)
   private static int witheredEmeraldEnchantability = 42;
   
   public static ToolMaterial witherEmerald;

   private static final MaterialHelper INSTANCE = new MaterialHelper();                                                                      
                                                                         
   private MaterialHelper()
   {
      HardcoreWither.config.register(this);
      witherEmerald = EnumHelper.addToolMaterial("WitheredEmerald", 
                                                 witheredEmeraldHarvestLevel, 
                                                 witheredEmeraldMaxUses, 
                                                 witheredEmeraldEfficiency, 
                                                 witheredEmeraldAttackDamage, 
                                                 witheredEmeraldEnchantability);
   }
   
   public static MaterialHelper getInstance()
   {
      return INSTANCE;
   }
}
