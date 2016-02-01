package thor12022.hardcorewither.proxies;

public class ClientProxy extends CommonProxy
{
   @Override
   public void preInit()
   {
      super.preInit();
      itemRegistry.registerModels();
   }
}
