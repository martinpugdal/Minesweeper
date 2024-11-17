package dk.martinersej.minesweeperv3.gameelement.elements;

import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.GameElementType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Placeholder extends GameElement {

    public Placeholder(Location location) {
        super(location, GameElementType.PLACEHOLDER);
    }

    @Override
    public ItemStack[] getBlockTypes() {
        // Light gray wool
        // Light gray stained clay
        // Gray wool
        // Gray stained clay
        return new ItemStack[]{
            new ItemStack(35, 1, (short) 8),
            new ItemStack(159, 1, (short) 8),
            new ItemStack(35, 1, (short) 7),
            new ItemStack(159, 1, (short) 7)
        };
    }
}
