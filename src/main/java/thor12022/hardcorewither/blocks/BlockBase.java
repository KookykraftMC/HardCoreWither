package thor12022.hardcorewither.blocks;

/*
 * Base block class for getting standard things done with quickly.
 * Extend this for pretty much every block you make.
 */

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block
{
   // If you are setting multiple textures for your block. IE: Metadata blocks.
   public BlockBase(String unlocName, Material material, SoundType soundType, float hardness)
   {
      super(material);

      setUnlocalizedName(unlocName);
      setRegistryName(ModInformation.ID + ":" + unlocName);
      setCreativeTab(HardcoreWither.tabBaseMod);
      setStepSound(soundType);
      setHardness(hardness);
   }
}
