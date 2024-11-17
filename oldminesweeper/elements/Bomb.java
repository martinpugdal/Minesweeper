package dk.martinersej.oldminesweeper.elements;

import dk.martinersej.oldminesweeper.GameElement;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Bomb extends GameElement {

    public Bomb(Location location) {
        super(location);
    }

    @Override
    public ItemStack[] getBlockTypes() {
        // cobblestone monster egg
        // mosssy cobblestone
        // polished granite
        // granite
        return new ItemStack[]{
                new ItemStack(97, 1, (short) 1),
                new ItemStack(48, 1),
                new ItemStack(1, 1, (short) 2),
                new ItemStack(1, 1, (short) 1)
        };
    }
}
