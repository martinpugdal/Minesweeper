package dk.martinersej.oldminesweeper.elements;

import dk.martinersej.oldminesweeper.GameElement;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Number extends GameElement {

    private final int number;

    public Number(Location location, int number) {
        super(location);
        if (number < 1 || number > 8) {
            throw new IllegalArgumentException("Number must be between 1 and 8");
        }
        this.number = number;
    }

    @Override
    public ItemStack[] getBlockTypes() {
        switch (number) {
            case 1:
                // blue wool
                // blue stained clay
                // cyan stained clay
                // cyan wool
                return new ItemStack[]{
                        new ItemStack(35, 1, (short) 11),
                        new ItemStack(159, 1, (short) 11),
                        new ItemStack(159, 1, (short) 9),
                        new ItemStack(35, 1, (short) 9)
                };
            case 2:
                // lime wool
                // green wool
                // green stained clay
                // lime stained clay
                return new ItemStack[]{
                        new ItemStack(35, 1, (short) 5),
                        new ItemStack(35, 1, (short) 13),
                        new ItemStack(159, 1, (short) 13),
                        new ItemStack(159, 1, (short) 5)
                };
            case 3:
                // red stained clay
                // red wool
                // pink wool
                // pink stained clay
                return new ItemStack[]{
                        new ItemStack(159, 1, (short) 14),
                        new ItemStack(35, 1, (short) 14),
                        new ItemStack(35, 1, (short) 6),
                        new ItemStack(159, 1, (short) 6)
                };
            case 4:
                // orange stained clay
                // yellow stained clay
                // yellow wool
                // orange wool
                return new ItemStack[]{
                        new ItemStack(159, 1, (short) 1),
                        new ItemStack(159, 1, (short) 4),
                        new ItemStack(35, 1, (short) 4),
                        new ItemStack(35, 1, (short) 1)
                };
            case 5:
                // magenta stained clay
                // purple stained clay
                // purple wool
                // magenta wool
                return new ItemStack[]{
                        new ItemStack(159, 1, (short) 2),
                        new ItemStack(159, 1, (short) 10),
                        new ItemStack(35, 1, (short) 10),
                        new ItemStack(35, 1, (short) 2)
                };
            case 6:
                // light blue stained clay
                // light blue wool
                // brown stained clay
                // brown wool
                return new ItemStack[]{
                        new ItemStack(159, 1, (short) 3),
                        new ItemStack(35, 1, (short) 3),
                        new ItemStack(159, 1, (short) 12),
                        new ItemStack(35, 1, (short) 12)
                };
            case 7:
                // black wool
                // lapis lazuli block
                // black stained clay
                // block of redstone
                return new ItemStack[]{
                        new ItemStack(35, 1, (short) 15),
                        new ItemStack(22, 1),
                        new ItemStack(159, 1, (short) 15),
                        new ItemStack(152, 1)
                };
            case 8:
                // block of coal
                // block of diamond
                // block of gold
                // block of emerald
                return new ItemStack[]{
                        new ItemStack(173, 1),
                        new ItemStack(57, 1),
                        new ItemStack(41, 1),
                        new ItemStack(133, 1)
                };
        }
        return new ItemStack[0];
    }
}
