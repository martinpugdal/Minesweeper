package dk.martinersej.minesweeper;

import dk.martinersej.minesweeper.command.ResetCommand;
import dk.martinersej.minesweeper.gameelement.GameElement;
import dk.martinersej.minesweeper.gameelement.elements.*;
import dk.martinersej.minesweeper.gameelement.elements.Number;
import dk.martinersej.minesweeper.listeners.GameListener;
import dk.martinersej.minesweeper.listeners.TexturePackListener;
import dk.martinersej.minesweeper.utils.GameWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public final class Minesweeper extends JavaPlugin {

    // faces to fill
    private static final BlockFace[] FILL_FACES = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
        BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST
    };

    private static Minesweeper instance;
    private final Map<Location, Bomb> bombs = new HashMap<>();
    private final Map<Location, Flag> flags = new HashMap<>();
    private final Map<Location, GameElement> reveals = new HashMap<>();
    private final Map<Location, Placeholder> placeholders = new HashMap<>();

    public static Minesweeper get() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Create game world - should happens on the first join
//        GameWorld.getInstance().createWorld();
//        setupFirstReveal(GameWorld.getInstance().getWorld().getSpawnLocation());

        // Register listeners
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new TexturePackListener(), this);

        // Register commands
        getCommand("reset").setExecutor(new ResetCommand());
    }

    @Override
    public void onDisable() {
        GameWorld.getInstance().deleteGameWorld();
        // Plugin shutdown logic
    }

    Map<String, Cell> cells = new HashMap<>();
    HashSet<String> mines = new HashSet<>();

    public Cell getCell(int x, int z) {
        String key = getKey(x,z);

        if(cells.containsKey(key))
            return cells.get(key);

        boolean isMine = mines.contains(key);
        Cell cell = new Cell(x, z, isMine);

        for(int dx = -1; dx <= 1; dx++) {
            for(int dz = -1; dz <= 1; dz++) {
                if(dx == 0 && dz == 0) continue;
                // Vi får vel det grid ved siden af også refeactorToGrid får den "realle lokation"
                int[] grid = refactorToGrid(x+(dx*2), z+(dz*2));
                String neighborKey = getKey(grid[0], grid[1]);
                if(mines.contains(neighborKey)) {
                    cell.setMineCount(cell.getMineCount() + 1);
                }
            }
        }

        return cells.put(key, cell);
    }

    public static String getKey(int x, int z) {
        return x + "," + z;
    }

    public void revealCell(int x, int z) {
        Cell cell = getCell(x, z);

        if(cell.isRevealed()) return;
        if(cell.isMine()) {
            // BOMBE;
            return;
        }

        cell.setRevealed(true);

        if(cell.getMineCount() == 0) {
            for(int dx = -1; dx <= 1; dx++) {
                for(int dz = -1; dz <= 1; dz++) {
                    int[] grid = refactorToGrid(x, z);
                    revealCell(grid[0], grid[1]); // javel pops
                }
            }
        }
    }

    private int[] refactorToGrid(int x, int z) {
        // Refactor the location to the nearest 2x2 grid aka original key location
        return new int[]{x - (x % 2), z - (z % 2)};
    }

    public void addBomb(Location location) {
        location = refactorToGrid(location);
        bombs.put(location, new Bomb(location));
    }

    public void placeFlag(Location location) {
        location = refactorToGrid(location);

        if (isRevealed(location)) {
            return;
        }

        if (flags.containsKey(location)) {
            Flag flag = flags.get(location);
            flags.remove(location);
            flag.remove();
            return;
        }

        Flag flag = new Flag(location);
        flag.reveal();
        flags.put(location, flag);
    }

    public boolean isRevealed(Location location) {
        location = refactorToGrid(location);
        return reveals.containsKey(location);
    }

    public boolean isPlaceholder(Location location) {
        location = refactorToGrid(location);
        return placeholders.containsKey(location);
    }

    public boolean isBomb(Location location) {
        location = refactorToGrid(location);
        return bombs.containsKey(location);
    }

    public GameElement getElementAtLoc(Location location) {
        location = refactorToGrid(location);

        if (isBomb(location)) {
            return bombs.get(location);
        }

        List<Bomb> nearbyBombs = getNearbyBombs(location);
        if (!nearbyBombs.isEmpty()) {
            return new Number(location, nearbyBombs.size());
        }

        if (reveals.containsKey(location)) {
            return reveals.get(location);
        }

        return new Blank(location);
    }

    public void revealElement(GameElement element) {
        if (reveals.containsKey(element.getFirstLocation())) {
            return;
        }

        element.reveal();

        if (element instanceof Blank) {
            fillBlocksInside(element.getFirstLocation()); // Fill surrounding blocks if the element is blank
        }

        // if element is placeholder, don't add it to reveals
        if (element instanceof Placeholder) {
            placeholders.put(element.getFirstLocation(), (Placeholder) element);
        } else {
            reveals.put(element.getFirstLocation(), element);
        }

        // remove flag if it's a flag
        flags.remove(element.getFirstLocation());
        // remove placeholder if it's a placeholder
        placeholders.remove(element.getFirstLocation());

        // place a placeholder element around the element if it's a number
        for (Location loc : getSurroundingLocations(element.getFirstLocation())) {
            if (reveals.containsKey(loc) || placeholders.containsKey(loc)) {
                continue;
            }

            Placeholder placeholder = new Placeholder(loc);
            placeholder.reveal();
            placeholders.put(loc, placeholder);
        }
    }

    private List<Location> getSurroundingLocations(Location location) {
        List<Location> surroundingLocations = new ArrayList<>();
        for (BlockFace face : FILL_FACES) {
            Vector vector = new Vector(face.getModX(), face.getModY(), face.getModZ());
            vector.multiply(2); // Move by 2 blocks to match the 2x2 grid pattern

            Location adjacentLoc = location.clone().add(vector);
            surroundingLocations.add(adjacentLoc);
        }
        return surroundingLocations;
    }

    public List<Bomb> getNearbyBombs(Location location) {
        // get nearby bombs in a 3x3 area around the location
        List<Bomb> nearbyBombs = new ArrayList<>();

        location = refactorToGrid(location);

        // we know element's x is even and z is even because of the way we generated the world
        for (int x = location.getBlockX() - 2; x <= location.getBlockX() + 2; x += 2) {
            for (int z = location.getBlockZ() - 2; z <= location.getBlockZ() + 2; z += 2) {
                Location loc = new Location(location.getWorld(), x, location.getBlockY(), z);

                // skip the location itself
                if (loc.equals(location)) {
                    continue;
                }

                if (bombs.containsKey(loc)) {
                    nearbyBombs.add(bombs.get(loc));
                }
            }
        }

        return nearbyBombs;
    }

    public void reset() {
        bombs.clear();
        reveals.clear();
        flags.clear();
        placeholders.clear();

        GameWorld.getInstance().deleteGameWorld();
        GameWorld.getInstance().createWorld();

        setupFirstReveal(GameWorld.getInstance().getWorld().getSpawnLocation());

        // tp all players to the new world
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(GameWorld.getInstance().getWorld().getSpawnLocation());
        }
    }

    public void setupFirstReveal(Location location) {
        location = refactorToGrid(location);
        List<Bomb> nearbyBombs = getNearbyBombs(location);

        // cant be bombs nearby
        if (!nearbyBombs.isEmpty()) {
            nearbyBombs.forEach(bomb -> {
                bombs.remove(bomb.getFirstLocation());
            });
        }

        fillBlocksInside(location);
    }

    private void fillBlocksInside(Location startLoc) {
        Set<Location> toFill = new HashSet<>();
        Set<Location> unrevealed = new HashSet<>();
        Set<Location> visited = new HashSet<>();
        startLoc = refactorToGrid(startLoc);

        // start the flood fill algorithm
        floodFill(startLoc, toFill, unrevealed, visited);

        for (Location loc : toFill) {
            GameElement element = getElementAtLoc(loc);
            revealElement(element);
        }
        for (Location loc : unrevealed) {
            placePlaceholderElementAt(loc);
        }
    }

    private void floodFill(Location loc, Set<Location> toFill, Set<Location> unrevealed, Set<Location> visited) {
        // Stop if the location has already been visited, revealed, or is out of bounds
        if (visited.contains(loc) ||
            reveals.containsKey(loc) ||
            flags.containsKey(loc) ||
            placeholders.containsKey(loc) ||
            toFill.contains(loc) ||
            unrevealed.contains(loc)
        ) {
            return;
        }

        visited.add(loc);

        // Ensure the location is within game bounds
        if (loc.getBlockX() % 2 != 0 || loc.getBlockZ() % 2 != 0) {
            return;
        }

        // Interact with the element to determine if it's a blank or bomb
        GameElement element = getElementAtLoc(loc);
        if (element instanceof Bomb) {
            unrevealed.add(loc);
            //placePlaceholderElementAt(loc);
            return; // Stop filling if it's a bomb
        }

        toFill.add(loc);

        if (element instanceof Number) {
            unrevealed.add(loc);
            return; // Stop filling if it's a number
        }

        // Iterate over each BlockFace and fill surrounding blocks
        for (BlockFace face : FILL_FACES) {
            Vector vector = new Vector(face.getModX(), face.getModY(), face.getModZ());
            vector.multiply(2); // Move by 2 blocks to match the 2x2 grid pattern

            Location adjacentLoc = loc.clone().add(vector);
            floodFill(adjacentLoc, toFill, unrevealed, visited);
        }
    }

    private void placePlaceholderElementAt(Location loc) {
        // Place a placeholder element at the location
        // This is used to prevent the flood fill algorithm from filling the location again
        // Iterate over each BlockFace and fill surrounding blocks
        for (BlockFace face : FILL_FACES) {
            Vector vector = new Vector(face.getModX(), face.getModY(), face.getModZ());
            vector.multiply(2); // Move by 2 blocks to match the 2x2 grid pattern

            Location adjacentLoc = loc.clone().add(vector);
            if (reveals.containsKey(adjacentLoc) || placeholders.containsKey(adjacentLoc)) {
                continue;
            }

            Placeholder placeholder = new Placeholder(adjacentLoc);
            revealElement(placeholder);
            placeholders.put(adjacentLoc, placeholder);
        }
    }
}
