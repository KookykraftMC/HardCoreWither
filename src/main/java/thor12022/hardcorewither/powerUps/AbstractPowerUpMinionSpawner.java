package thor12022.hardcorewither.powerUps;

import java.util.Map;

import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.ConfigManager;
import thor12022.hardcorewither.config.Configurable;
import thor12022.hardcorewither.entity.EntityBlazeMinion;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

@Configurable
public abstract class AbstractPowerUpMinionSpawner extends AbstractPowerUp
{
   protected class WitherMinionSpawner extends MobSpawnerBaseLogic
   {
      
      final private EntityWither ownerWither;
      
      public WitherMinionSpawner(EntityWither theOwnerWither)
      {
         ownerWither = theOwnerWither;
      }
      
      public void func_98267_a(int p_98267_1_)
      {
         // This would usually register a block update event with the world
      }

      public World getSpawnerWorld()
      {
         return ownerWither.worldObj;
      }

      public int getSpawnerX()
      {
         return (int) Math.round(ownerWither.lastTickPosX);
      }

      public int getSpawnerY()
      {
         return (int) Math.round(ownerWither.lastTickPosY) + 1;
      }

      public int getSpawnerZ()
      {
         return (int) Math.round(ownerWither.lastTickPosZ);
      }
   }

   @Config
   public String  entityLocalizedName = "Pig";
   
   @Config(maxInt = 65535)
   public int delay = 20;
   
   @Config(maxInt = 65535)
   public int playerRange = 48;
   
   @Config(maxInt = 65535)
   public int maxEntities = 6;
   
   @Config(maxInt = 65535)
   public int minDelay = 600;
   
   @Config(maxInt = 65535)
   public int maxDelay = 800;

   @Config(maxInt = 65535)
   public int spawnCount = 4;

   @Config(maxInt = 65535)
   public int spawnRange = 4;

   private final WitherMinionSpawner spawner;
    
   @Config(minFloat = 0f, maxFloat = 10f, comment = "Amount to increase Spawn Count by. 1.0 to never increase")
   static protected float spawnCountModifier = 1.1f;
   
   @Config(minFloat = 0f, maxFloat = 1f, comment = "The smaller it is, the faster the delay decrease. 1.0 to never decrease")
   static protected float spawnDelayModifier = 0.8f;
   
   @Config(minFloat = 1f, maxFloat = 10f, comment = "Amount to increase Max Entities by. 1.0 to never increase")
   static protected float maxEntitiesModifier = 1.1f;
   
   public AbstractPowerUpMinionSpawner(int minLevel, int maxStrength)
   {
      super(minLevel, maxStrength);
      spawner = null;
   }
   
   protected AbstractPowerUpMinionSpawner(EntityWither theOwnerWither, String entityLocalizedName)
   {
      super(theOwnerWither);
      spawner = new WitherMinionSpawner(ownerWither);
      this.entityLocalizedName = entityLocalizedName;
      ResetSpawnerToData();
   }

   protected void ResetSpawnerToData()
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
   public void updateWither()
   {
      spawner.updateSpawner();
   }

   @Override
   public boolean increasePower()
   {
      if(super.increasePower())
      {
         spawnCount *= spawnCountModifier;
         delay *= spawnDelayModifier;
         minDelay *=  spawnDelayModifier;
         maxDelay *=  spawnDelayModifier;
         maxEntities *=  maxEntitiesModifier;
         ResetSpawnerToData();
         return true;
      }
      return false;
   };
   
   @Override
   public void readFromNBT(NBTTagCompound nbt)
   {
      super.readFromNBT(nbt);
      spawnCount *= (spawnCountModifier * super.powerStrength);
      delay *= (spawnDelayModifier * super.powerStrength);
      minDelay *=  (spawnDelayModifier * super.powerStrength);
      maxDelay *=  (spawnDelayModifier * super.powerStrength);
      maxEntities *=  (maxEntitiesModifier * super.powerStrength);
      ResetSpawnerToData();
   }
}
