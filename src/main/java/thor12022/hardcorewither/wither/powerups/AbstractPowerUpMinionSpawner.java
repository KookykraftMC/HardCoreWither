package thor12022.hardcorewither.wither.powerups;

import thor12022.hardcorewither.api.IPowerUpStateData;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Configurable
abstract class AbstractPowerUpMinionSpawner extends AbstractPowerUp
{
   protected static class Data extends IPowerUpStateData
   {
      EntityWither theOwnerWither;
      
      private String entityLocalizedName;
      private int    delay;
      private int    playerRange;
      private int    maxEntities;
      private int    minDelay;
      private int    maxDelay;
      private int    spawnCount;
      private int    spawnRange;
      
      MobSpawnerBaseLogic spawner = new MobSpawnerBaseLogic()
      {          
         @Override
         public void func_98267_a(int p_98267_1_)
         {
            // This would usually register a block update event with the world
         }

         @Override
         public World getSpawnerWorld()
         {
            return theOwnerWither.worldObj;
         }

         @Override
         public BlockPos getSpawnerPosition()
         {
            return theOwnerWither.getPosition();
         }
      };
      
      Data(EntityWither wither)
      {
         theOwnerWither = wither;
      }
      
      protected void resetSpawnerToData()
      {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setString("EntityId", entityLocalizedName);
         nbt.setShort("Delay", (short)delay);
         nbt.setShort("RequiredPlayerRange", (short)playerRange);
         nbt.setShort("MaxNearbyEntities", (short)maxEntities);
         nbt.setShort("MinSpawnDelay", (short)minDelay);
         nbt.setShort("MaxSpawnDelay", (short)maxDelay);
         nbt.setShort("SpawnCount", (short)spawnCount);
         nbt.setShort("SpawnRange", (short)spawnRange);
         spawner.readFromNBT(nbt);
      }

      @Override
      public NBTTagCompound serializeNBT()
      {
         NBTTagCompound nbt = new NBTTagCompound();
         NBTTagCompound spawnerNbt = new NBTTagCompound();
         spawner.writeToNBT(spawnerNbt);
         nbt.setTag("spawnerLogic", spawnerNbt);
         return nbt;
      }

      @Override
      public void deserializeNBT(NBTTagCompound nbt)
      {
         NBTTagCompound spawnerNbt = nbt.getCompoundTag("spawnerLogic");
         spawner.readFromNBT(spawnerNbt);
      }
   }
   
   final protected String ENTITY_LOCALIZED_NAME;
   
   @Config(maxInt = 65535)
   protected int defaultDelay = 20;
   
   @Config(maxInt = 65535)
   protected int defaultPlayerRange = 48;
   
   @Config(maxInt = 65535)
   protected int defaultMaxEntities = 6;
   
   @Config(maxInt = 65535)
   protected int defaultMinDelay = 600;
   
   @Config(maxInt = 65535)
   protected int defaultMaxDelay = 800;

   @Config(maxInt = 65535)
   protected int defaultSpawnCount = 4;

   @Config(maxInt = 65535)
   protected int defaultSpawnRange = 4;
    
   @Config(minFloat = 1f, maxFloat = 10f, comment = "Amount to increase Spawn Count by. 1.0 to never increase")
   static protected float spawnCountModifier = 1.1f;
   
   @Config(minFloat = 0f, maxFloat = 1f, comment = "The smaller it is, the faster the delay decrease. 1.0 to never decrease")
   static protected float spawnDelayModifier = 0.8f;
   
   @Config(minFloat = 1f, maxFloat = 10f, comment = "Amount to increase Max Entities by. 1.0 to never increase")
   static protected float maxEntitiesModifier = 1.1f;
   
   public AbstractPowerUpMinionSpawner(int minLevel, int maxStrength, String entityLocalizedName)
   {
      super(minLevel, maxStrength);
      this.ENTITY_LOCALIZED_NAME = entityLocalizedName;
   }
   
   @Override
   public void updateWither(EntityWither wither, int strength, IPowerUpStateData data)
   {
      ((Data)data).spawner.updateSpawner();
   }

   @Override
   public void witherDied(EntityWither wither, int strength, IPowerUpStateData data)
   {}

   @Override
   public IPowerUpStateData applyPowerUp(EntityWither wither, int strength)
   {
      Data data = new Data(wither);
      data.entityLocalizedName = ENTITY_LOCALIZED_NAME;
      data.spawnCount = Math.round(defaultSpawnCount * spawnCountModifier);
      data.delay = Math.round(defaultDelay * spawnDelayModifier);
      data.minDelay = Math.round(defaultMinDelay * spawnDelayModifier);
      data.maxDelay = Math.round(defaultMaxDelay * spawnDelayModifier);
      data.maxEntities = Math.round(defaultMaxEntities * maxEntitiesModifier);
      data.playerRange = defaultPlayerRange;
      data.spawnRange = defaultSpawnRange;
      data.resetSpawnerToData();
      return data;
   }

}
