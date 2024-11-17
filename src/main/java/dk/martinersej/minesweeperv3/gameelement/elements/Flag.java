package dk.martinersej.minesweeperv3.gameelement.elements;

import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.GameElementType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Flag extends GameElement {

    public Flag(Location location) {
        super(location, GameElementType.FLAG);
    }

    public static ItemStack getFlagStack() {
        return new ItemStack(Material.BLAZE_POWDER);
    }
}
