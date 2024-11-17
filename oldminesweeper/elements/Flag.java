package dk.martinersej.oldminesweeper.elements;

import dk.martinersej.oldminesweeper.GameElement;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Flag extends GameElement {

    public Flag(Location location) {
        super(location);
    }

    @Override
    public ItemStack[] getBlockTypes() {
        // andesite
        // polished andesite
        // polished diorite
        // diorite
        return new ItemStack[]{
                new ItemStack(1, 1, (short) 5),
                new ItemStack(1, 1, (short) 6),
                new ItemStack(1, 1, (short) 4),
                new ItemStack(1, 1, (short) 3)
        };
    }

    public void remove() {
        // remove the flag by undo the flag placement by revealing the placeholder
        Placeholder placeholder = new Placeholder(getFirstLocation());
        placeholder.reveal();
    }
}
