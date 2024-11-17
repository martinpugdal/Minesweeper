package dk.martinersej.minesweeper;

import org.bukkit.Location;

public class Cell {

    private final int x, z;

    private final boolean isMine;

    private int mineCount;

    private boolean revealed;

    private boolean flagged;

    public Cell(int x, int z, boolean isMine) {
        this.x = x;
        this.z = z;
        this.isMine = isMine;
        this.mineCount = 0;
        this.revealed = false;
        this.flagged = false;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getKey() {
        return Minesweeper.getKey(x,z);
    }

    public boolean isMine() {
        return this.isMine;
    }

    public int getMineCount() {
        return this.mineCount;
    }
    public void setMineCount(int mines) {
        this.mineCount = mines;
    }

    public boolean isRevealed() {
        return revealed;
    }
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
