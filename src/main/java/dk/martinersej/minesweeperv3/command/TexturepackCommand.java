package dk.martinersej.minesweeperv3.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dk.martinersej.minesweeperv3.listeners.TexturePackListener.RESOURCE_PACK_URL;

public class TexturepackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        player.setResourcePack(RESOURCE_PACK_URL);

        return true;
    }
}
