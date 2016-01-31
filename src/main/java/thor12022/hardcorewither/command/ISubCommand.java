package thor12022.hardcorewither.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public interface ISubCommand
{
   public void processCommand(ICommandSender sender, String[] args, int startingIndex) throws WrongUsageException;
}
