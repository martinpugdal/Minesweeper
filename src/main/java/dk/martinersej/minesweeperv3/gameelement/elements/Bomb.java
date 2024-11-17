package dk.martinersej.minesweeperv3.gameelement.elements;

import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.GameElementType;
import org.bukkit.Location;

public class Bomb extends GameElement {

    private boolean isRevealed = false;

    public Bomb(Location location) {
        super(location, GameElementType.BOMB);
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }
}
