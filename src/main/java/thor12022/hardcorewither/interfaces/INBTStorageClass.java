package thor12022.hardcorewither.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBTStorageClass extends INBTSerializable<NBTTagCompound>
{
   @Override
   public NBTTagCompound serializeNBT();
   
   @Override
   public void deserializeNBT(NBTTagCompound nbt);
   
   public void resetNBT();
}
