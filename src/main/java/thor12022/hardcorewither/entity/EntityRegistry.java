package thor12022.hardcorewither.entity;

import thor12022.hardcorewither.HardcoreWither;

public class EntityRegistry
{
   public void register()
   {
int id = 0;
      net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
            EntityBlazeMinion.class,
            EntityBlazeMinion.UNLOCALIZED_NAME,
            id++, 
            HardcoreWither.instance, 
            64, 
            10, 
            true);
      net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
            EntityGhastMinion.class,
            EntityGhastMinion.UNLOCALIZED_NAME,
            id++, 
            HardcoreWither.instance, 
            64, 
            10, 
            true);
      net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
            EntitySkeletonMinion.class,
            EntitySkeletonMinion.UNLOCALIZED_NAME,
            id++, 
            HardcoreWither.instance, 
            64, 
            10, 
            true);
   }
}
