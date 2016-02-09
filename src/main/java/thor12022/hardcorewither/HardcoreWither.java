package thor12022.hardcorewither;

import thor12022.hardcorewither.client.gui.CreativeTabBaseMod;
import thor12022.hardcorewither.command.CommandManager;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.proxies.CommonProxy;
import thor12022.hardcorewither.util.TextHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION, dependencies = ModInformation.DEPEND)
public class HardcoreWither
{

   @Mod.Instance
   public static HardcoreWither INSTANCE;
   
   @SidedProxy(clientSide = ModInformation.CLIENTPROXY, serverSide = ModInformation.COMMONPROXY)
   public static CommonProxy proxy;

   public static CreativeTabs tabBaseMod = new CreativeTabBaseMod(ModInformation.ID + ".creativeTab");
   public static Logger logger = LogManager.getLogger(ModInformation.NAME);

   public static ConfigManager config;
   
   public static final Random RAND = new Random();

   @Mod.EventHandler
   public void preInit(FMLPreInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.preInit"));

      config = new ConfigManager(event.getSuggestedConfigurationFile());

      proxy.preInit();
   }

   @Mod.EventHandler
   public void init(FMLInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.init"));

      proxy.init();
   }

   @Mod.EventHandler
   public void serverStarting(FMLServerStartingEvent event)
   {
      CommandManager.serverStarting(event);
   }

}
