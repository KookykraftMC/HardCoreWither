package thor12022.hardcorewither;

/*
 * Check all the classes for (hopefully) detailed descriptions of what it does. There will also be tidbits of comments throughout the codebase.
 * If you wish to add a description to a class, or extend/change an existing one, submit a PR with your changes.
 */

//import biomesoplenty.api.content.BOPCBiomes;
import java.io.File;

import thor12022.hardcorewither.blocks.BlockRecipeRegistry;
import thor12022.hardcorewither.blocks.BlockRegistry;
import thor12022.hardcorewither.client.gui.CreativeTabBaseMod;
import thor12022.hardcorewither.client.gui.GuiHandler;
import thor12022.hardcorewither.command.CommandManager;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.entity.EntityRegistry;
import thor12022.hardcorewither.items.ItemRecipeRegistry;
import thor12022.hardcorewither.items.ItemRegistry;
import thor12022.hardcorewither.handlers.PlayerHandler;
import thor12022.hardcorewither.handlers.TinkersConstructHandler;
import thor12022.hardcorewither.potions.PotionRegistry;
import thor12022.hardcorewither.powerUps.PowerUpManager;
import thor12022.hardcorewither.proxies.CommonProxy;
import thor12022.hardcorewither.EventHandler;
import thor12022.hardcorewither.util.TextHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import scala.Array;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION, dependencies = ModInformation.DEPEND)
public class HardcoreWither
{
   @SidedProxy(clientSide = ModInformation.CLIENTPROXY, serverSide = ModInformation.COMMONPROXY)
   public static CommonProxy proxy;

   public static CreativeTabs tabBaseMod = new CreativeTabBaseMod(ModInformation.ID + ".creativeTab");
   public static Logger logger = LogManager.getLogger(ModInformation.NAME);

   @Mod.Instance
   public static HardcoreWither instance;

   private PowerUpManager           powerUpManager =  new PowerUpManager();
   private PlayerHandler            playerHandler  =  new PlayerHandler();;
   private DataStoreManager         dataStore      =  new DataStoreManager(ModInformation.CHANNEL);
   private EventHandler             eventHandler   =  new EventHandler(playerHandler, powerUpManager);
   private ItemRegistry             itemRegistry   =  new ItemRegistry();
   private BlockRegistry            blockRegistry  =  new BlockRegistry();
   private PotionRegistry           potionRegistry =  new PotionRegistry();
   private EntityRegistry           entityRegistry =  new EntityRegistry();
   private TinkersConstructHandler  tiCoRegistry   =  new TinkersConstructHandler();

   public HardcoreWither()
   {
      dataStore.addStorageClass(playerHandler, "PlayerHandler");
      dataStore.addStorageClass(powerUpManager, "witherData");
   }

   @Mod.EventHandler
   public void preInit(FMLPreInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.preInit"));

      powerUpManager.init();
      
      ConfigManager.getInstance().init(event.getSuggestedConfigurationFile());

      itemRegistry.registerItems();
      blockRegistry.registerBlocks();
      potionRegistry.registerPotions();
      entityRegistry.register();

      MinecraftForge.EVENT_BUS.register(eventHandler);
   }

   @Mod.EventHandler
   public void init(FMLInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.init"));

      ItemRecipeRegistry.registerItemRecipes();
      BlockRecipeRegistry.registerBlockRecipes();

      if(Loader.isModLoaded("TConstruct"))
      {
         tiCoRegistry.init(event);
      }
   }

   @Mod.EventHandler
   public void postInit(FMLPostInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.postInit"));
   }

   @Mod.EventHandler
   public void serverStarting(FMLServerStartingEvent event)
   {
      CommandManager.serverStarting(event);
   }

   @Mod.EventHandler
   public void onFMLServerStartedEvent(FMLServerStartedEvent event)
   {
   }
}
