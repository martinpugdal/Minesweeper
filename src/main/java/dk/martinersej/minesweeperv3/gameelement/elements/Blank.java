package dk.martinersej.minesweeperv3.gameelement.elements;

import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.GameElementType;
import org.bukkit.Location;

public class Blank extends GameElement {

    public Blank(Location location) {
        super(location, GameElementType.BLANK);
    }
}
