package dk.martinersej.minesweeper.gameelement.elements;

import dk.martinersej.minesweeper.gameelement.GameElement;
import dk.martinersej.minesweeper.gameelement.GameElementType;
import org.bukkit.Location;

public class Blank extends GameElement {

    public Blank(Location location) {
        super(location, GameElementType.BLANK);
    }
}
