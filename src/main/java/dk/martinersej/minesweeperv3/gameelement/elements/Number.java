package dk.martinersej.minesweeperv3.gameelement.elements;

import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.GameElementType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Number extends GameElement {

    private final int number;

    public Number(Location location, int number) {
        super(location, GameElementType.NUMBER);
        if (number < 1 || number > 8) {
            throw new IllegalArgumentException("Number must be between 1 and 8");
        }
        this.number = number;
    }

    @Override
    public ItemStack[] getBlockTypes() {
        return super.getType().getBlockTypes(number);
    }
}
