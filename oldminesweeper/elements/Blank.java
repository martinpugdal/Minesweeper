package dk.martinersej.oldminesweeper.elements;

import dk.martinersej.oldminesweeper.GameElement;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Blank extends GameElement {

    public Blank(Location location) {
        super(location);
    }

    @Override
    public ItemStack[] getBlockTypes() {
        // dark oak wood (162:1)
        // cracked stone bricks
        // mossy stone bricks
        // iron block
        return new ItemStack[]{
            new ItemStack(162, 1, (short) 1),
            new ItemStack(98, 1, (short) 2),
            new ItemStack(98, 1, (short) 1),
            new ItemStack(42, 1)
        };
    }
}
