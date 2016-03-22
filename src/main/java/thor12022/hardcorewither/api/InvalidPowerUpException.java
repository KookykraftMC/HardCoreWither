package thor12022.hardcorewither.api;

public class InvalidPowerUpException extends Exception
{
   private final String name;
   
   InvalidPowerUpException(String name)
   {
      this.name = name;
   }

   
   @Override
   public String getMessage()
   {
      return "Id(" + name + ")not for a valid PowerUp";
   }
}
