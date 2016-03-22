package thor12022.hardcorewither;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.interfaces.INBTStorageClass;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DataStoreManager
{
   private String dataStoreName;
   private File saveFile = null;   
   private Map<INBTStorageClass, String> storageClasses = new HashMap<INBTStorageClass, String>();
   
   public DataStoreManager(String dataStoreName)
   {
      this.dataStoreName = dataStoreName;
      MinecraftForge.EVENT_BUS.register(this);
   }
   
   public void addStorageClass( INBTStorageClass theClass, String tagName )
   {
      storageClasses.put(theClass, tagName);
   }
   
   private void getDataFile()
   {
      File worldConfig = DimensionManager.getCurrentSaveRootDirectory();
      File hardcoreWitherFolder = new File( worldConfig.getPath(), ModInformation.CHANNEL );
      if( !hardcoreWitherFolder.isDirectory() && !hardcoreWitherFolder.mkdir() )
      {
         HardcoreWither.LOGGER.error("Failed to create " + hardcoreWitherFolder.getAbsolutePath() + " data will not save" );
      }
      else
      {
         saveFile = new File(hardcoreWitherFolder, dataStoreName + ".dat");
         try
         {
            if( !saveFile.exists() && !saveFile.createNewFile() )
            {
               HardcoreWither.LOGGER.error("Failed to create " + saveFile.getAbsolutePath() + " data will not save" );
               saveFile = null;
            }
            else
            {
               HardcoreWither.LOGGER.debug("Data file: " + saveFile.getAbsolutePath() + " found/created" );
            }
         } catch (IOException e)
         {
            HardcoreWither.LOGGER.error("Failed to create " + saveFile.getAbsolutePath() + " data will not save" );
            saveFile = null;
         }
      }
   }
      
   @SubscribeEvent
   public void onWorldSave(  Save event )
   {
      // we only need to do this once per Save, not per level
      if( event.world.provider.getDimensionId() != 0 || event.world.isRemote)
      {
         return;
      }
      if( saveFile == null )
      {
         HardcoreWither.LOGGER.error("Cannot save data" );
      }
      else
      {
         try
         {
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            NBTTagCompound globalNbt = new NBTTagCompound();
            Iterator iter = storageClasses.keySet().iterator();
            while (iter.hasNext()) 
            {
               NBTTagCompound classNbt = new NBTTagCompound();
               INBTStorageClass theClass = (INBTStorageClass)iter.next();
               theClass.writeToNBT(classNbt);
               globalNbt.setTag(storageClasses.get(theClass), classNbt);
            }
            CompressedStreamTools.writeCompressed(globalNbt, fileOutputStream );
            fileOutputStream.close();
            HardcoreWither.LOGGER.debug("Saved data" );
         }
         catch( Throwable e )
         {
            HardcoreWither.LOGGER.error("Error saving data: " + e.getLocalizedMessage());
         }
      }
   }
   
   @SubscribeEvent
   public void onWorldUnload(Unload event)
   {
      // we only need to do this once per Save, not per level
      if( event.world.provider.getDimensionId() != 0 || event.world.isRemote)
      {
         return;
      }
      if( saveFile != null )
      {
         for(INBTStorageClass starageClass : storageClasses.keySet())
         {
            starageClass.resetNBT();
         }
         saveFile = null;
      }
   }
   
   @SubscribeEvent
   public void onWorldLoad(Load event)
   {
      // we only need to do this once per Save and only on the server
      if( event.world.provider.getDimensionId() != 0  || event.world.isRemote)
      {
         return;
      }
      getDataFile();
      if( saveFile == null )
      {
         HardcoreWither.LOGGER.error("Cannot load data" );
      }
      else
      {
         try
         {
            FileInputStream fileInputStream = new FileInputStream( saveFile );
            NBTTagCompound globalNbt = CompressedStreamTools.readCompressed( fileInputStream );
            Iterator iter = storageClasses.keySet().iterator();
            while (iter.hasNext()) 
            {
               INBTStorageClass theClass = (INBTStorageClass)iter.next();
               theClass.readFromNBT(globalNbt.getCompoundTag(storageClasses.get(theClass)));
            }
            HardcoreWither.LOGGER.debug("Data loaded" );
            fileInputStream.close();
         }
         catch( Throwable e )
         {
            HardcoreWither.LOGGER.warn("Failed to  load data " + e.getLocalizedMessage() + ", hopefully a new world.");
         }
      }
   }
}
