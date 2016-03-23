package thor12022.hardcorewither.api;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class WitherAffinityRegistry
{
   static final private Collection<Item> ITEMS =  new HashSet<Item>();

   /**
    * @return false if registration failed, or duplicate Item
    * @pre The server has not been started yet (Constructing, Preinit, or init)
    * @todo what about sub-items?
    */
   public static boolean register(Item item)
   {
      if(!Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START) && item != null)
      {
         return ITEMS.add(item);
      }
      
      return false;
   }
   
   /**
    * @return a collection of all Items that have been registered
    */
   public static final Collection<Item> getAll()
   {
      return ITEMS;
   }
   
   /**
    * @return true if the given Item has been registered
    */
   public static boolean isRegistered(Item item)
   {
      return ITEMS.contains(item);
   }
      
}
