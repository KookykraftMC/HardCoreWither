package thor12022.hardcorewither.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.interfaces.INBTStorageClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHandler implements INBTStorageClass
{
   private Map<UUID, Double> playerXp  = new HashMap<UUID, Double>();

   public PlayerHandler()
   {
      HardcoreWither.DATA_STORE.addStorageClass(this, "PlayerHandler");
   }
   
   public double wasAtWitherSpawn(EntityPlayer player)
   {
      return addWitherExperience(player, 1.0 );
   }
   
   private double addWitherExperience( EntityPlayer player, double wxp )
   {
      double prevXp = 0.0;
      if( playerXp.containsKey(player.getUniqueID()) )
      {
         prevXp = playerXp.get(player.getUniqueID());
      }
      playerXp.put(player.getUniqueID(), prevXp + wxp);
      return prevXp + wxp;
   }
   
   @Override
   public void writeToNBT(NBTTagCompound nbt)
   {
      for(UUID uuid : playerXp.keySet())
      {
         nbt.setDouble(uuid.toString(), playerXp.get(uuid));
      }
   }
   
   @Override
   public void readFromNBT(NBTTagCompound nbt)
   {
      for(String tag : nbt.getKeySet()) 
      {
         playerXp.put( UUID.fromString(tag), nbt.getDouble(tag));
      }
   }

   @Override
   public void resetNBT()
   {
      playerXp.clear();
      
   }
}
