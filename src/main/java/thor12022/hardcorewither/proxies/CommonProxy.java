package thor12022.hardcorewither.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import thor12022.hardcorewither.DataStoreManager;
import thor12022.hardcorewither.EventHandler;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.blocks.BlockRecipeRegistry;
import thor12022.hardcorewither.blocks.BlockRegistry;
import thor12022.hardcorewither.entity.EntityRegistry;
import thor12022.hardcorewither.handlers.PlayerHandler;
import thor12022.hardcorewither.handlers.TinkersConstructHandler;
import thor12022.hardcorewither.handlers.WitherHandler;
import thor12022.hardcorewither.items.ItemRegistry;
import thor12022.hardcorewither.items.MaterialHelper;
import thor12022.hardcorewither.potions.PotionRegistry;
import thor12022.hardcorewither.powerUps.PowerUpManager;

public class CommonProxy
{
   protected ItemRegistry              itemRegistry;
   protected PowerUpManager            powerUpManager;
   protected BlockRegistry             blockRegistry;
   protected PotionRegistry            potionRegistry;
   protected EntityRegistry            entityRegistry;
   protected TinkersConstructHandler   tiCoRegistry;
   protected WitherHandler             witherHandler;
   protected EventHandler              eventHandler;
   protected PlayerHandler             playerHandler;
   protected DataStoreManager          dataStore;
   
   
    
   public void preInit()
   {
      construct();
      
      dataStore.addStorageClass(playerHandler, "PlayerHandler");
      dataStore.addStorageClass(powerUpManager, "witherData");
      
      powerUpManager.init();
      
      itemRegistry.registerItems();
      blockRegistry.registerBlocks();
      potionRegistry.registerPotions();
      entityRegistry.register();
      
      MinecraftForge.EVENT_BUS.register(eventHandler);
   }
   
   public void init()
   {
      itemRegistry.registerRecipes();
      
      BlockRecipeRegistry.registerBlockRecipes();
      
      if(Loader.isModLoaded("tconstruct"))
      {
         tiCoRegistry.init();
      }
   }
   
   protected void construct()
   {
      assert(HardcoreWither.config != null);
      
      itemRegistry   =  new ItemRegistry();
      powerUpManager =  new PowerUpManager();
      blockRegistry  =  new BlockRegistry();
      potionRegistry =  new PotionRegistry();
      entityRegistry =  new EntityRegistry();
      tiCoRegistry   =  new TinkersConstructHandler();
      witherHandler  =  new WitherHandler();
      eventHandler   =  new EventHandler(playerHandler, powerUpManager);
      playerHandler  =  new PlayerHandler();
      dataStore      =  new DataStoreManager(ModInformation.CHANNEL);
      
      MaterialHelper.getInstance();
   }
   
}
