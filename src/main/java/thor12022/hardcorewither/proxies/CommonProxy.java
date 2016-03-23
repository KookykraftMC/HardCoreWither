package thor12022.hardcorewither.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import thor12022.hardcorewither.EventHandler;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.blocks.BlockRecipeRegistry;
import thor12022.hardcorewither.blocks.BlockRegistry;
import thor12022.hardcorewither.compatability.TinkersConstructHandler;
import thor12022.hardcorewither.compatability.WitherChargingHandler;
import thor12022.hardcorewither.entity.EntityRegistry;
import thor12022.hardcorewither.handlers.WitherAffinityHandler;
import thor12022.hardcorewither.items.ItemRegistry;
import thor12022.hardcorewither.items.MaterialHelper;
import thor12022.hardcorewither.potions.PotionRegistry;
import thor12022.hardcorewither.powerUps.WitherHandler;

public class CommonProxy
{
   protected ItemRegistry              itemRegistry;
   protected BlockRegistry             blockRegistry;
   protected PotionRegistry            potionRegistry;
   protected EntityRegistry            entityRegistry;
   protected TinkersConstructHandler   tiCoRegistry;
   protected WitherAffinityHandler     witherAffinityHandler;
   protected WitherChargingHandler     witherChargingHandler;
   protected WitherHandler             witherHandler;
   protected EventHandler              eventHandler;   
    
   public void preInit()
   {
      construct();
                  
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
      assert(HardcoreWither.CONFIG != null);
      
      itemRegistry            =  new ItemRegistry();
      blockRegistry           =  new BlockRegistry();
      potionRegistry          =  new PotionRegistry();
      entityRegistry          =  new EntityRegistry();
      tiCoRegistry            =  new TinkersConstructHandler();
      witherAffinityHandler   =  new WitherAffinityHandler();
      witherChargingHandler   =  new WitherChargingHandler();
      witherHandler           =  new WitherHandler();
      eventHandler            =  new EventHandler();
      
      MaterialHelper.getInstance();
   }
   
}
