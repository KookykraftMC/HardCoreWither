package thor12022.hardcorewither.wither;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thor12022.hardcorewither.config.Config;
import thor12022.hardcorewither.config.Configurable;

@Configurable(sectionName="WitherAffinity")
class WitherAffinityHelper
{
   private static final String NBT_WITHER_AFFINITY = "witherAffinity";
   
   @Config
   private static boolean isEnabled = true;
   
   @Config(minInt=1)
   private static int levelingBase = 300;

   @Config(minFloat=1f)
   private static float levelingMultiplier = 2.5f;
   
   @Config(comment="is s, the enchant level is l, and the chance is c: where s/(-1*l-s) = c")
   private static float powerUpRemovalChanceScale = 12f;
   
   static int getWitherAffinityXp(ItemStack stack)
   {
      NBTTagCompound tag = stack.getTagCompound();
      return tag.getInteger(NBT_WITHER_AFFINITY);
   }
   
   static void setWitherAffinityXp(ItemStack stack, int xp)
   {
      NBTTagCompound tag = stack.getTagCompound();
      tag.setInteger(NBT_WITHER_AFFINITY, xp);
   }
   
   static float chanceCalc(int currentLevel)
   {
      return powerUpRemovalChanceScale / (-1 * currentLevel - powerUpRemovalChanceScale);
   }
   
   static int xpCalc(int currentLevel)
   {
      return currentLevel * (levelingBase + (int)((levelingBase - 1) * levelingMultiplier ));
   }
   
   static boolean shouldWitherAffinityLevelUp(ItemStack stack, int currentLevel)
   {
      return getWitherAffinityXp(stack) >= xpCalc(currentLevel);
   }
   
   
}
