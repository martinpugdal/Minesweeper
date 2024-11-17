package dk.martinersej.minesweeper.gameelement.elements;

import dk.martinersej.minesweeper.gameelement.GameElement;
import dk.martinersej.minesweeper.gameelement.GameElementType;
import org.bukkit.Location;

public class Bomb extends GameElement {

    public Bomb(Location location) {
        super(location, GameElementType.BOMB);
    }
}
