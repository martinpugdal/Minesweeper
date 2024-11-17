package dk.martinersej.minesweeper.gameelement;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class GameElement {

    // has 4 locations/vectors
    private final Location[] locations = new Location[4];
    private final GameElementType type;

    public GameElement(Location location, GameElementType type) {
        // set the first location to the given location
        locations[0] = location;
        // set the other 3 locations to the given location
        locations[1] = location.clone().add(new Vector(1, 0, 0));
        locations[2] = location.clone().add(new Vector(0, 0, 1));
        locations[3] = location.clone().add(new Vector(1, 0, 1));
        this.type = type;
    }

    public GameElementType getType() {
        return type;
    }

    public ItemStack[] getBlockTypes() {
        return type.getBlockTypes(0); // default blocktypes
    }

    public Location getFirstLocation() {
        return locations[0];
    }

    public Location[] getLocations() {
        return locations;
    }

    public void reveal() {
        // set the block type of all 4 locations to the block type of the first location
        for (int i = 0; i < getBlockTypes().length; i++) {
            locations[i].getBlock().setType(getBlockTypes()[i].getType());
            locations[i].getBlock().setData(getBlockTypes()[i].getData().getData());
            locations[i].getBlock().getState().update();
        }
    }
}
