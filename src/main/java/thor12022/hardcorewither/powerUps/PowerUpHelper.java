package thor12022.hardcorewither.powerUps;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.BlockPos;
import thor12022.hardcorewither.command.AbstractSubCommand;
import thor12022.hardcorewither.command.CommandManager;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.api.IPowerUp;
import thor12022.hardcorewither.api.PowerUpRegistry;

public class PowerUpHelper
{   
   static
   {
      if(!PowerUpRegistry.register(new PowerUpBlazeMinionSpawner()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpBlazeMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpDeathKnell()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpDeathKnell.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpGhastMinionSpawner()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpGhastMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpHealthBoost()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpHealthBoost.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpSkeletonMinionSpawner()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpSkeletonMinionSpawner.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpLightning()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpLightning.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpTeleport()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpTeleport.class.getName());
      }
      if(!PowerUpRegistry.register(new PowerUpDamageResistance()))
      {
         HardcoreWither.logger.warn("Cannot register " + PowerUpDamageResistance.class.getName());
      }
   }
   
   AbstractSubCommand spawnCommand = new AbstractSubCommand()
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
            HardcoreWither.logger.debug("PowerUp Command Formatting Error (probably) not accounted for " + excp);
            throw new WrongUsageException(getCommandUsage(sender));
         }
      }
   };
   
   /**
    * Default constructor
    */
   public PowerUpHelper()
   {
      CommandManager.getInstance().registerSubCommand(spawnCommand);
   }
   
   /**
    * Apply an amount of Power Ups to a certain Wither
    * @param witherData apply power ups to this
    * @param sizeOfPowerUp this many levels of Power Ups
    * @note wither should not have had Power Ups applied to it already
    */
   void powerUpWither(WitherData witherData, int sizeOfPowerUp)
   {
      if(witherData.getActivePowerUpEffects() == 0)
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
                  HardcoreWither.logger.debug("Increasing power of " + powerUp.getClass());
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
               HardcoreWither.logger.debug("Adding " + powerUp.getName());
            }
         }
         witherData.setPoweredUp(true);
      }
      else
      {
         HardcoreWither.logger.debug("Attempting to re-powerup Wither");
      }
   }
}
