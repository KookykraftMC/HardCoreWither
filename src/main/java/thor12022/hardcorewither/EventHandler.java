package thor12022.hardcorewither;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thor12022.hardcorewither.HardcoreWither;
import thor12022.hardcorewither.ModInformation;
import thor12022.hardcorewither.util.TextHelper;

public class EventHandler
{
   public EventHandler()
   {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
   {
      if(eventArgs.modID.equals(ModInformation.ID))
      {
         HardcoreWither.config.syncConfig();
         HardcoreWither.logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.config.refresh"));
      }
   }
}
