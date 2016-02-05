package thor12022.hardcorewither.handlers;

import java.util.Random;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.potions.PotionAntiWither;
import thor12022.hardcorewither.potions.PotionRegistry;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Configurable(sectionName = "TinkersConstruct")
public class TinkersConstructHandler
{
   @Config
   private static boolean enableGreenHeartCanister = true;
   
   @Config
   private static boolean enableGreenHeartWitherDrop = true;
   
   @Config(minInt = 0, maxInt = 10)
   private static int greenHeartDropRarity = 2;
   
   private static Item heartCanister = null;
   
   public TinkersConstructHandler()
   {
     ConfigManager.getInstance().register(this);
	  MinecraftForge.EVENT_BUS.register(this);
   }
   
   public void init(FMLInitializationEvent event)
   {

      HardcoreWither.logger.info("Tinkers' Constuct Support Initalizing");
      if(enableGreenHeartCanister)
      {
         if(heartCanister == null)
         {
            heartCanister = GameRegistry.findItem("tconstruct", "heartCanister");
         }
         
         if(heartCanister != null)
         {
            GameRegistry.addShapelessRecipe( new ItemStack(heartCanister, 1, 6), 
                                             new ItemStack(heartCanister, 1, 4), 
                                             new ItemStack(heartCanister, 1, 5), 
                                             new ItemStack(Items.nether_star));
         }
         else
         {
            MinecraftForge.EVENT_BUS.unregister(this);
            HardcoreWither.logger.warn("Cannot find TConstruct:heartCanister, disabling Tinkers' Construct support");
         }
      }
   }
   
   @SubscribeEvent
   public void onLivingDrop (LivingDropsEvent event)
   {
       if (!enableGreenHeartWitherDrop || !event.recentlyHit)
       {
          return;
       }
       //FeedTheCreeperTweaks.logger.debug("EntityLiving Dropping something");
       if (event.entityLiving != null && event.entityLiving.getClass() == EntityWither.class)
       {
          //FeedTheCreeperTweaks.logger.debug("It was a Wither");
          if(event.source.damageType.equals("player"))
          {
             //FeedTheCreeperTweaks.logger.debug("A Player did it");
             EntityPlayer killerPlayer = (EntityPlayer)event.source.getEntity();
             PotionEffect antiWitherEffect = killerPlayer.getActivePotionEffect(PotionRegistry.potionAntiWither);
             if( antiWitherEffect != null )
             {
                //FeedTheCreeperTweaks.logger.debug("They used Anti-Wither!");
                if(PotionAntiWither.HasEntityBeenWithered(killerPlayer))
                {
                   //FeedTheCreeperTweaks.logger.debug("They were withered!");
                   Random rand = new Random();
                   int numberOfHearts = 0;
                   for( int lootingLevel = event.lootingLevel; lootingLevel > 0; --lootingLevel)
                   {
                      numberOfHearts += rand.nextInt(greenHeartDropRarity) == 0 ? 1 : 0;
                   }
                   EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(heartCanister, numberOfHearts, 5));
                   //! @todo check if a replacement for this is needed
                   //entityitem.delayBeforeCanPickup = 10;
                   event.drops.add(entityitem);
                   HardcoreWither.logger.debug("Withered Anti-Withered Player killed Wither, dropping Miniture" + numberOfHearts + " Green Hearts");
                }
             }
             
          }
       }
    }
}
