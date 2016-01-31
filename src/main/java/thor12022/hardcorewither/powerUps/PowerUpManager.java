package thor12022.hardcorewither.powerUps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import thor12022.hardcorewither.command.AbstractSubCommand;
import thor12022.hardcorewither.command.CommandManager;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.interfaces.INBTStorageClass;

@Configurable
public class PowerUpManager implements INBTStorageClass
{
   static final int NBT_FORMAT = 1;
   private static final Random RANDOM =  new Random();
   
   private class WitherData
   {
      ArrayList<IPowerUp>  powerUps             =  new ArrayList();
      int                  strength             =  0;
      boolean              hasDied              =  false;
      boolean              isPendingLootDrops   =  false;
      
      Collection<IPowerUp> getPowerUps()
      {
         return powerUps;
      }
      
      boolean increasePowerUp(IPowerUp powerUp)
      {
         for(IPowerUp usedPowerUp : powerUps)
         {
            if(usedPowerUp.getClass() == powerUp.getClass())
            {
               return usedPowerUp.increasePower();
            }
         }
         return false;
      }
      
      boolean hasPowerUp(IPowerUp powerUp)
      {
         for(IPowerUp usedPowerUp : powerUps)
         {
            if(usedPowerUp.getClass() == powerUp.getClass())
            {
               return true;
            }
         }
         return false;
      }
      
      void addPowerUp(IPowerUp powerUp)
      {
         powerUps.add(powerUp);
      }
   }
   
   private Map<String, IPowerUp>       powerUpPrototypes       =  new HashMap();
   private Map<UUID, WitherData>       activeWitherData        =  new HashMap();
   private Map<UUID, NBTTagCompound>   savedWitherDataNbt      =  new HashMap();
   private int                         largestPowerUp          =  0;

   @Config(comment="Will enable the Looting enchant on weapons to affect the Wither's drops")
   private boolean witherLooting = true;
   
   @Config
   private boolean netherStarLooting = true;
   
   @Config(minFloat = 0f, comment="Scales the amount of reward in relation to Wither strength (non-linear)")
   private float lootingLevelMultiplier = 2f;
   
   @Config(minFloat=0f)
   private float netherStarLootingMultiplier = .5f;
   
    AbstractSubCommand spawnCommand = new AbstractSubCommand()
   {
      @Override
      public final String getCommandUsage(ICommandSender sender)
      {
         String text = super.getCommandUsage(sender);
         Iterator iter = powerUpPrototypes.keySet().iterator();
         while(iter.hasNext())
         {
            text += iter.next() + "\n";
         }
         return text;
      }

      @Override
      public final String getCommandName()
      {
         return "spawn";
      }

      @Override
      public void processCommand(ICommandSender sender, String[] args, int startingIndex) throws WrongUsageException
      {
         if( args.length < startingIndex + 2 )
         {
            throw new WrongUsageException(getCommandUsage(sender));
         }
         else
         {
            NBTTagCompound nbt = new NBTTagCompound();
            EntityWither spawnedWither = new EntityWither(sender.getEntityWorld());
            try
            {
               NBTTagCompound powerUpList = new NBTTagCompound();
               for(int argIndex = startingIndex; argIndex < args.length - 1; argIndex += 2)
               {
                  String powerUpName = args[argIndex];
                  int powerUpStrength = parseInt(args[argIndex + 1], 1);
                  IPowerUp powerUpPrototype = powerUpPrototypes.get(powerUpName);
                  IPowerUp powerUp = powerUpPrototype.createPowerUp(spawnedWither);
                  // @todo should be able to set or create with correct strength
                  for(; powerUpStrength > 1; --powerUpStrength)
                  {
                     powerUp.increasePower();
                  }
                  NBTTagCompound powerUpNbt = new NBTTagCompound();
                  powerUp.writeToNBT(powerUpNbt);
                  powerUpList.setTag(powerUpName, powerUpNbt);
               }
               nbt.setTag("powerUps", powerUpList);
            }
            catch(Exception excp)
            {
               // ok, so this is kinda the Lazy Man's way of making sure nothing really goes wrong
               HardcoreWither.logger.debug("PowerUp Command Formatting Error (probably) not accounted for " + excp);
               throw new WrongUsageException(getCommandUsage(sender));
            }
            spawnedWither.func_82206_m();
            BlockPos coords = sender.getPosition();
            spawnedWither.setPosition(coords.getX(), coords.getY(), coords.getZ());
            savedWitherDataNbt.put(spawnedWither.getUniqueID(), nbt);
            sender.getEntityWorld().spawnEntityInWorld(spawnedWither);
         }
      }
   };
   
   /**
    * Default constructor
    */
   public PowerUpManager()
   {
      CommandManager.getInstance().registerSubCommand(spawnCommand);
      ConfigManager.getInstance().register(this);
      MinecraftForge.EVENT_BUS.register(this);
   }
   
   public void init()
   {
      registerPowerUp(new PowerUpBlazeMinionSpawner());
      registerPowerUp(new PowerUpDeathKnell());
      registerPowerUp(new PowerUpGhastMinionSpawner());
      registerPowerUp(new PowerUpHealthBoost());
      registerPowerUp(new PowerUpSkeletonMinionSpawner());
      registerPowerUp(new PowerUpLightning());
      //registerPowerUp(new PowerUpSpeedBoost());
      registerPowerUp(new PowerUpTeleport());
   }
   
   /**
    * Registers a Power Up with the Power Up Manager
    * @param powerUp a default-constructed Power Up to use as a prototype
    *    for constructing further Power Ups of its type
    * @pre powerUp is not of a class that has already been registered
    */
   public void registerPowerUp( IPowerUp powerUp)
   {
      if(!powerUpPrototypes.containsKey(powerUp.getName()))
      {
         powerUpPrototypes.put(powerUp.getName(), powerUp);
         HardcoreWither.logger.info("Registering Prototype for " + powerUp.getName().toString());  
      }
      else
      {
         HardcoreWither.logger.debug("Duplicate Prototype registered for " + powerUp.getName().toString());
      }
   }
   
   /**
    * Apply an amount of Power Ups to a certain Wither
    * @param wither apply power ups to this
    * @param sizeOfPowerUp this many levels of Power Ups
    * @note wither should not have had Power Ups applied to it already
    */
   public void powerUpWither(EntityWither wither, int sizeOfPowerUp)
   {
      if(savedWitherDataNbt.containsKey(wither.getUniqueID()))
      {
         loadWitherFromNBT(wither, savedWitherDataNbt.get(wither.getUniqueID()));
         // now that it's loaded, we don't need to worry about its NBT data anymore
         savedWitherDataNbt.remove(wither.getUniqueID());
      }
      else if(!activeWitherData.containsKey(wither.getUniqueID()))
      {
         activeWitherData.put(wither.getUniqueID(), new WitherData());
         WitherData witherData = activeWitherData.get(wither.getUniqueID());
         int powerUpSize = sizeOfPowerUp != -1 ? sizeOfPowerUp : largestPowerUp + 1;
         Collection<IPowerUp> validPowerUpPrototypes = powerUpPrototypes.values();
         int usedStrength = 0;
         while(usedStrength < sizeOfPowerUp && validPowerUpPrototypes.size() > 0)
         {
            IPowerUp powerUpPrototpe = (IPowerUp) validPowerUpPrototypes.toArray()[RANDOM.nextInt(validPowerUpPrototypes.size())];
            if(powerUpPrototpe.minPower() > sizeOfPowerUp)
            {
               validPowerUpPrototypes.remove(powerUpPrototpe);
            }
            else if(witherData.hasPowerUp(powerUpPrototpe))
            {
               if( witherData.increasePowerUp(powerUpPrototpe) )
               {
                  ++usedStrength;
                  HardcoreWither.logger.debug("Increasing power of " + powerUpPrototpe.getClass());
               }
               else
               {
                  validPowerUpPrototypes.remove(powerUpPrototpe);
               }
            }
            // If this is a new powerup for this Wither
            else
            {
               IPowerUp newPowerUp = powerUpPrototpe.createPowerUp(wither);
               if(newPowerUp != null)
               {
                  witherData.addPowerUp(newPowerUp);
                  usedStrength += powerUpPrototpe.minPower() > 0 ? powerUpPrototpe.minPower() : 1;
                  HardcoreWither.logger.debug("Adding " + powerUpPrototpe.getName());
               }
            }
         }
         witherData.strength = usedStrength;
         if(powerUpSize > largestPowerUp)
         {
            largestPowerUp = powerUpSize;
         }
      }
      else
      {
         HardcoreWither.logger.debug("Attempting to re-powerup Wither");
      }
   }
   
   public void update(EntityWither wither)
   {
      if(activeWitherData.containsKey(wither.getUniqueID()))
      {
         // Iterate through the powerups for this Wither
         for(IPowerUp powerUp : activeWitherData.get(wither.getUniqueID()).getPowerUps())
         {
            powerUp.updateWither();
         }
      }
   }  
   
   public void witherLootDrops(LivingDropsEvent event)
   {
      WitherData witherData = activeWitherData.get(event.entityLiving.getUniqueID());
      // If we have not seen this Wither Die yet
      if(witherData != null && !witherData.isPendingLootDrops)
      {
         witherData.isPendingLootDrops = true;
         // Recalculate the looting based upon Wither strength 
         int lootingLevel = (witherLooting ? event.lootingLevel : 0) + (int)Math.round( Math.log10(witherData.strength + 1) * lootingLevelMultiplier);
         // and resend the drops event
         if(!ForgeHooks.onLivingDrops(event.entityLiving, event.source, (ArrayList)event.drops, lootingLevel, event.recentlyHit))
         {
            //then do the same thing EntityLivingBase does
            for (EntityItem item : event.drops)
            {
               event.entityLiving.worldObj.spawnEntityInWorld(item);
            }
            //! @note I don't like doing this, as far as the Forge Event manager is concerned the death event was cancelled
            //!   and that could have some side effects. However, the only other way I can think of at the moment is to 
            //!   use Reflection to modify the lootingLevel field of the LivingDropsEvent, which seems even worse.
         }
         // then cancel this one
         event.setCanceled(true);
      }
      // This is the resent event
      else if(witherData != null)
      {
         witherData.isPendingLootDrops = false;
         
         if (netherStarLooting && event.lootingLevel > 0)
         {
             int j = 0;

             j += Math.round(Math.abs(this.RANDOM.nextGaussian() * event.lootingLevel * netherStarLootingMultiplier));

             if( j > 0)
             {
                EntityItem entityItem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(Items.nether_star, j, 0));
                //! @todo see if replacement is needed 
                //entityItem.delayBeforeCanPickup = 10;
                event.drops.add(entityItem);
             }
         }   
         
         activeWitherData.remove(event.entityLiving.getUniqueID());
      }
   }
   
   public void witherDied(EntityWither wither)
   {
      WitherData witherData = activeWitherData.get(wither.getUniqueID());
      if(witherData != null)
      {
         // Iterate through the powerups for this Wither
         for(IPowerUp powerUp : witherData.getPowerUps())
         {
            powerUp.witherDied();
         }
         // can't remove it until the drops are handled
         witherData.hasDied = true;
      }

      // if the data was invalid it might still be hanging out in here
      savedWitherDataNbt.remove(wither.getUniqueID());
   }

   public boolean isWitherPoweredUp(EntityWither wither)
   {
      return activeWitherData.get(wither.getUniqueID()) != null;
   }
   
   /**
    * ROOT
    * +--formatVersion : n
    * +--witherListNbt
    * |  +--UUID1
    * |  |  +--totalStrength : n
    * |  |  +--hasDied : n
    * |  |  +--pendingDrops : n
    * |  |  +--totalStrength : n
    * |  |  +--powerUpList
    * |  |  |  +--PowerUp1
    * |  |  |  |  +--{powerup defined}
    * |  |  |  +--PowerUpN
    * |  +--UUIDN
    * +--largestPowerUp : n  
    */
   @Override
   public void writeToNBT(NBTTagCompound nbt)
   {
      //! @todo I feel like NBTTagList might be of use here
      NBTTagCompound witherListNbt = new NBTTagCompound();
      for( UUID witherUuid : activeWitherData.keySet())
      {
         NBTTagCompound witherNbt = new NBTTagCompound();
         witherNbt.setInteger("totalStrength", activeWitherData.get(witherUuid).strength);
         witherNbt.setBoolean("hasDied", activeWitherData.get(witherUuid).hasDied);
         witherNbt.setBoolean("isPendingLootDrops", activeWitherData.get(witherUuid).isPendingLootDrops);
         NBTTagCompound powerUpListNbt = new NBTTagCompound();
         for( IPowerUp powerUp : activeWitherData.get(witherUuid).getPowerUps())
         {
            String powerUpName = powerUp.getName();
            NBTTagCompound powerUpNbt = new NBTTagCompound();
            powerUp.writeToNBT(powerUpNbt);
            powerUpListNbt.setTag(powerUpName, powerUpNbt);
         }
         witherNbt.setTag("powerUps", powerUpListNbt);
         witherListNbt.setTag(witherUuid.toString(), witherNbt);
      }
      // Withers that haven't been loaded yet
      for( UUID witherUuid : savedWitherDataNbt.keySet())
      {
         witherListNbt.setTag(witherUuid.toString(), savedWitherDataNbt.get(witherUuid));
      }
      nbt.setInteger("formatVersion", NBT_FORMAT);
      nbt.setTag("witherListNbt", witherListNbt);
      nbt.setInteger("largestPowerUp", largestPowerUp );
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt)
   {
      int formatVersion = nbt.getInteger("formatVersion");
      //! @todo I feel like NBTTagList might be of use here
      NBTTagCompound witherListNbt = (NBTTagCompound) nbt.getTag("witherListNbt");
      for(String witherUuidString : (Collection<String>)witherListNbt.getKeySet())
      {
         UUID witherUuid = UUID.fromString(witherUuidString);
         NBTTagCompound witherNbt = (NBTTagCompound) witherListNbt.getTag(witherUuidString);
         if(witherNbt != null)
         {
            if(formatVersion == NBT_FORMAT)
            {
               // we have no way to look up Withers by UUID until the chunk they are in gets loaded
               savedWitherDataNbt.put(witherUuid, witherNbt);
            }
            else
            {
               NBTTagCompound newWitherNbt = new NBTTagCompound();
               NBTTagCompound powerUpListNbt = new NBTTagCompound();
               for(String powerUpName : (Collection<String>)witherNbt.getKeySet()) 
               {
                  powerUpListNbt.setTag(powerUpName, witherNbt.getTag("powerUpName"));
               }
               newWitherNbt.setTag("powerUps", powerUpListNbt);
               newWitherNbt.setInteger("totalStrength", powerUpListNbt.getKeySet().size());
               newWitherNbt.setBoolean("hasDied", false);
               newWitherNbt.setBoolean("isPendingLootDrops", false);
               // we have no way to look up Withers by UUID until the chunk they are in gets loaded
               savedWitherDataNbt.put(witherUuid, newWitherNbt);
            }
         }
         else
         {
            HardcoreWither.logger.warn("Cannot load Wither for " + witherUuidString);
         }
      }
      largestPowerUp = nbt.getInteger("largestPowerUp");      
   }  
   
   /**
    * 
    * @param wither
    * @param nbt
    * 
    * ROOT 
    * +--formatVersion : n
    * +--witherListNbt
    * |  +--UUID1
    * |  |  +--totalStrength : n
    * |  |  +--hasDied : n
    * |  |  +--pendingDrops : n
    * |  |  +--powerUpList
    * |  |  |  +--PowerUp1
    * |  |  |  |  +--{powerup defined}
    * |  |  |  +--PowerUpN
    * |  +--UUIDN
    * +--largestPowerUp : n 
    */
   private void loadWitherFromNBT(EntityWither wither, NBTTagCompound nbt)
   {
      WitherData witherData = new WitherData();
      activeWitherData.put(wither.getUniqueID(), witherData);
      witherData.strength = nbt.getInteger("totalStrength");
      witherData.hasDied = nbt.getBoolean("hasDied");
      witherData.isPendingLootDrops = nbt.getBoolean("pendingDrops");
      NBTTagCompound powerUpList = nbt.getCompoundTag("powerUps");
      for(String powerUpName : (Collection<String>)powerUpList.getKeySet()) 
      {
         try 
         {
            if(powerUpPrototypes.containsKey(powerUpName))
            {
               NBTTagCompound powerUpNbt = (NBTTagCompound) powerUpList.getTag(powerUpName);
               IPowerUp powerUp = powerUpPrototypes.get(powerUpName).createPowerUp(wither);
               powerUp.readFromNBT(powerUpNbt);
               activeWitherData.get(wither.getUniqueID()).addPowerUp(powerUp);
            }
         }
         catch (Exception ex)
         {
            HardcoreWither.logger.warn("Attempting to powerup from save with unknown powerup: " + powerUpName + "\n\t" + ex);
         }
      
      }
   }

   @Override
   public void resetNBT()
   {
      activeWitherData.clear();
      savedWitherDataNbt.clear();
      largestPowerUp =  0;      
   }
}
