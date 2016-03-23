package thor12022.hardcorewither.wither;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.BlockPos;
import thor12022.hardcorewither.command.AbstractSubCommand;
import thor12022.hardcorewither.command.CommandManager;
import thor12022.hardcorewither.wither.powerups.PowerUpBlazeMinionSpawner;
import thor12022.hardcorewither.wither.powerups.PowerUpDamageResistance;
import thor12022.hardcorewither.wither.powerups.PowerUpDeathKnell;
import thor12022.hardcorewither.wither.powerups.PowerUpEffect;
import thor12022.hardcorewither.wither.powerups.PowerUpGhastMinionSpawner;
import thor12022.hardcorewither.wither.powerups.PowerUpHealthBoost;
import thor12022.hardcorewither.wither.powerups.PowerUpLightning;
import thor12022.hardcorewither.wither.powerups.PowerUpSkeletonMinionSpawner;
import thor12022.hardcorewither.wither.powerups.PowerUpTeleport;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUp;
import thor12022.hardcorewither.api.PowerUpRegistry;

class PowerUpHelper
{   
   static void initialize()
   {
      if(!PowerUpRegistry.register(new PowerUpBlazeMinionSpawner()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpBlazeMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpDeathKnell()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpDeathKnell.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpGhastMinionSpawner()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpGhastMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpHealthBoost()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpHealthBoost.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpSkeletonMinionSpawner()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpSkeletonMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpLightning()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpLightning.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpTeleport()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpTeleport.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpDamageResistance()))
      {
         HardcoreWither.LOGGER.warn("Cannot register " + PowerUpDamageResistance.class.getName());
      }
      
      CommandManager.getInstance().registerSubCommand(spawnCommand);
   }
   
   static AbstractSubCommand spawnCommand = new AbstractSubCommand()
   {
      @Override
      public final String getCommandUsage(ICommandSender sender)
      {
         String text = super.getCommandUsage(sender);
         for(IPowerUp powerUp : PowerUpRegistry.getAll())
         {
            text += powerUp.getName() + ", ";
         }
         text.substring(0, text.length() - 2);
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
         EntityWither spawnedWither = new EntityWither(sender.getEntityWorld());
         spawnedWither.func_82206_m();
         BlockPos coords = sender.getPosition();
         spawnedWither.setPosition(coords.getX(), coords.getY(), coords.getZ());
         sender.getEntityWorld().spawnEntityInWorld(spawnedWither);
         try
         {
            WitherData witherData = WitherData.getWitherData(spawnedWither);
            if(witherData != null)
            {
               for(int argIndex = startingIndex; argIndex < args.length - 1; argIndex += 2)
               {
                  String powerUpName = args[argIndex];
                  int powerUpStrength = parseInt(args[argIndex + 1], 1);
                  
                  witherData.addPowerUpEffect(powerUpName, powerUpStrength);
               }
            }
         }
         catch(Exception excp)
         {
            spawnedWither.worldObj.removeEntity(spawnedWither);
            // ok, so this is kinda the Lazy Man's way of making sure nothing really goes wrong
            HardcoreWither.LOGGER.debug("PowerUp Command Formatting Error (probably) not accounted for " + excp);
            throw new WrongUsageException(getCommandUsage(sender));
         }
      }
   };

   
   /**
    * Apply an amount of Power Ups to a certain Wither
    * @param witherData apply power ups to this
    * @param sizeOfPowerUp this many levels of Power Ups
    * @note wither should not have had Power Ups applied to it already
    */
   static void powerUpWither(WitherData witherData, int sizeOfPowerUp)
   {
      if(witherData.getActivePowerUpEffects().size() == 0)
      {
         int usedStrength = 0;
         ArrayList<IPowerUp> validPowerUps = new ArrayList<IPowerUp>(PowerUpRegistry.getAll());
         validPowerUps.removeIf(powerUp -> powerUp.minWitherLevel() > sizeOfPowerUp);
         validPowerUps.removeIf(powerUp -> powerUp.canApply(witherData.getWither()));
         while(usedStrength < sizeOfPowerUp && validPowerUps.size() > 0)
         {
            int powerUpIndex = HardcoreWither.RAND.nextInt(validPowerUps.size());
            IPowerUp powerUp = validPowerUps.get(powerUpIndex);
            PowerUpEffect powerUpEffect = witherData.getActivePowerUpEffect(powerUp);
            if(powerUpEffect != null)
            {
               int currentStength = powerUpEffect.getStrength();
               if( powerUpEffect.adjustEffect(currentStength + 1) == currentStength)
               {
                  ++usedStrength;
                  HardcoreWither.LOGGER.debug("Increasing power of " + powerUp.getClass());
               }
               else
               {
                  validPowerUps.remove(powerUpIndex);
               }
            }
            // If this is a new powerup for this Wither
            else
            {
               witherData.addPowerUpEffect(powerUp.getName(), 1);
               //! @todo this line should probably be different
               usedStrength += powerUp.minWitherLevel() > 0 ? powerUp.minWitherLevel() : 1;
               HardcoreWither.LOGGER.debug("Adding " + powerUp.getName());
            }
         }
         witherData.setPoweredUp(true);
      }
      else
      {
         HardcoreWither.LOGGER.debug("Attempting to re-powerup Wither");
      }
   }
   
   /**
    * Randomly removes one level from a random PowerUp the Wither has
    * @param witherData apply power ups to this
    * @pre wither should have had Power Ups applied to it already
    */
   static public void reduceWitherPowerUp(WitherData witherData)
   {
      int effectPosition = HardcoreWither.RAND.nextInt(witherData.getActivePowerUpEffects().size());
      int currIndex = 0;
      for(PowerUpEffect powerUpEffect : witherData.getActivePowerUpEffects())
      {
         if(currIndex++ == effectPosition)
         {
            if(powerUpEffect.getStrength() == 1)
            {
               witherData.removePowerUpEffect(powerUpEffect.getName());
            }
            else
            {
               powerUpEffect.adjustEffect(-1);
            }
            break;
         }
      }
   }
}
