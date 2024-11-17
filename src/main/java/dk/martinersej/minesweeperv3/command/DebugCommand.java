package dk.martinersej.minesweeperv3.command;

import dk.martinersej.minesweeperv3.Minesweeper;
import dk.martinersej.minesweeperv3.gameelement.GameElement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        GameElement gameElement = Minesweeper.get().getElementAtLoc(player.getTargetBlock(new HashSet<Material>(), 5).getLocation());

        if (gameElement == null)
            player.sendMessage("No game element found");
        else
            player.sendMessage("Game element: " + gameElement.getType().name());

        return true;
    }
}
