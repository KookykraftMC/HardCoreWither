package thor12022.hardcorewither.items;

public interface IItem
{
   public String name();
      
   public void registerItem();
   
   public void registerModel();
   
   public void registerRecipe();

   public boolean isEnabled();
}
