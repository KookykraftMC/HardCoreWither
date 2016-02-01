package thor12022.hardcorewither.proxies;

import thor12022.hardcorewither.items.ItemRegistry;

public class CommonProxy
{
   protected ItemRegistry itemRegistry = new ItemRegistry();
   
   public void preInit()
   {
      itemRegistry.registerItems();
   }
}
