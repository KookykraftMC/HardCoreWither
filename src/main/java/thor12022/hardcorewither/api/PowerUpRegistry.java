package thor12022.hardcorewither.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import thor12022.hardcorewither.api.IPowerUp;

public class PowerUpRegistry
{
   static private Map<String, IPowerUp> powerUps =  new HashMap<String, IPowerUp>();

   /**
    * @return false if registration failed, or duplicate PowerUp Name
    * @pre The server has not been started yet (Constructing, Preinit, or init)
    */
   public static boolean register(IPowerUp newPowerUp)
   {
      if(!Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START) && newPowerUp != null)
      {
         if(!powerUps.containsKey(newPowerUp.getName()))
         {
            powerUps.put(newPowerUp.getName(), newPowerUp);
            return true;
         }
      }
      
      return false;
   }
   
   /**
    * 
    * @return IPowerUp with given name
    * @throws InvalidPowerUpException for invalid name
    */
   public static final IPowerUp get(String name) throws InvalidPowerUpException
   {
      if(!powerUps.containsKey(name))
      {
         throw new InvalidPowerUpException(name);
      }
      
      return powerUps.get(name);
   }
   
   /**
    * @return a collection of all PowerUps
    */
   public static final Collection<IPowerUp> getAll()
   {
      return powerUps.values();
   }
}
