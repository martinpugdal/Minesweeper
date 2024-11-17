package dk.martinersej.oldminesweeper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Minesweeper.get().reset();
        commandSender.sendMessage("§aDu har lige resat spillet!");

        return true;
    }
}
