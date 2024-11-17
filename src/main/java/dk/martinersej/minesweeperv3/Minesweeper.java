package dk.martinersej.minesweeperv3;

import dk.martinersej.minesweeperv3.command.DebugCommand;
import dk.martinersej.minesweeperv3.command.ResetCommand;
import dk.martinersej.minesweeperv3.command.TexturepackCommand;
import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.elements.Number;
import dk.martinersej.minesweeperv3.gameelement.elements.*;
import dk.martinersej.minesweeperv3.listeners.GameListener;
import dk.martinersej.minesweeperv3.listeners.TexturePackListener;
import dk.martinersej.minesweeperv3.utils.GameWorld;
import dk.martinersej.minesweeperv3.utils.PacketUtils;
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
    private final Map<Location, GameElement> gameElements = new HashMap<>();
    private final Map<Location, Bomb> bombs = new HashMap<>();

    private final int lives = 3; // lives for the player before getting timeout
    private final int timeout = 60 * 10 * 1000; // timeout in ms

    private final int serverLives = 15;
    private final Map<UUID, Integer> playerLives = new HashMap<>();
    private final Map<UUID, Long> timeouts = new HashMap<>();
    private int currentServerLives = 15; // lives for the server before resetting the game

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
        getCommand("texturepack").setExecutor(new TexturepackCommand());
        getCommand("debug").setExecutor(new DebugCommand());

        timeoutTask();
    }

    @Override
    public void onDisable() {
        GameWorld.getInstance().deleteGameWorld();
        // Plugin shutdown logic
    }

    private Location refactorToGrid(Location location) {
        location = location.clone();
        if (location.getBlockX() % 2 != 0) {
            location.setX(location.getBlockX() - 1);
        }
        if (location.getBlockZ() % 2 != 0) {
            location.setZ(location.getBlockZ() - 1);
        }
        location.setY(0);
        return location;
    }

    public void placeFlag(Location location) {
        location = refactorToGrid(location);
        GameElement element = gameElements.get(location);

        if (/*element == null || */element instanceof Placeholder) {
            Flag flag = new Flag(location);
            revealElementWithoutFilling(flag);
        } else if (element instanceof Flag) {
            revealElementWithoutFilling(new Placeholder(location));
        }
    }

    public GameElement getElementAtLoc(Location location) {
        location = refactorToGrid(location);


        if (bombs.containsKey(location)) {
            return bombs.get(location);
        }

        List<Bomb> nearbyBombs = getNearbyBombs(location);
        if (!nearbyBombs.isEmpty()) {
            return new Number(location, nearbyBombs.size());
        }

        if (gameElements.containsKey(location)) {
            return gameElements.get(location);
        }

        return new Blank(location);
    }

    public GameElement getElementAtLocWithoutPlaceholder(Location location) {
        location = refactorToGrid(location);


        if (bombs.containsKey(location)) {
            return bombs.get(location);
        }

        List<Bomb> nearbyBombs = getNearbyBombs(location);
        if (!nearbyBombs.isEmpty()) {
            return new Number(location, nearbyBombs.size());
        }

        if (gameElements.containsKey(location) &&
            (!(gameElements.get(location) instanceof Placeholder) || gameElements.get(location) instanceof Flag)) {
            return gameElements.get(location);
        }

        return new Blank(location);
    }

    public void revealElement(Player player, GameElement element) {
        if (gameElements.get(element.getFirstLocation()) != null &&
            gameElements.get(element.getFirstLocation()) instanceof Flag
        ) {
            return; // needs to be removed before you can reveal this element
        }

        revealElementWithoutFilling(element);

        if (element instanceof Blank) {
            fillBlocksInside(element.getFirstLocation());
        } else {
            // surround the element with placeholders if not set by gameElements
            placePlaceholderElementAt(element.getFirstLocation());

            if (element instanceof Bomb) {
                Bomb bomb = (Bomb) element;
                if (!bomb.isRevealed()) {
                    bomb.setRevealed(true);
                    currentServerLives--;
                    int currentPlayerLives = playerLives.getOrDefault(player.getUniqueId(), lives);
                    playerLives.put(player.getUniqueId(), currentPlayerLives - 1);
                    currentPlayerLives--;
                    if (currentPlayerLives <= 0) {
                        timeoutPlayer(player);
                    } else {
                        player.sendMessage("§cDu har " + currentPlayerLives + " liv tilbage");
                    }
                    if (currentServerLives <= 0) {
                        reset();
                        Bukkit.broadcastMessage("§cServeren har nul liv tilbage, spillet er blevet nulstillet");
                    } else {
                        // if all players is timed out, reset the game
                        if (playerLives.isEmpty()) {
                            reset();
                            Bukkit.broadcastMessage("§cAlle spillere er ude, spillet er blevet nulstillet");
                            return;
                        }
                        Bukkit.broadcastMessage("§cDer er " + currentServerLives + " liv tilbage på serveren");
                        Bukkit.broadcastMessage("§c§o" + player.getName() + " var uheldig og ramte en bombe");
                    }
                }
            }
        }
    }

    private void timeoutPlayer(Player player) {
        playerLives.remove(player.getUniqueId());
        timeouts.put(player.getUniqueId(), System.currentTimeMillis() + timeout);
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
    }

    public void revealElementWithoutFilling(GameElement element) {
        gameElements.put(element.getFirstLocation(), element);
        element.reveal();
    }

    public void addBomb(Location location) {
        location = refactorToGrid(location);
        bombs.put(location, new Bomb(location));
    }

    public List<Bomb> getNearbyBombs(Location location) {
        // Get nearby bombs in a 3x3 area around the location
        List<Bomb> nearbyBombs = new ArrayList<>();

        location = refactorToGrid(location);

        // Loop through a 3x3 grid area, checking every second block
        for (int x = location.getBlockX() - 2; x <= location.getBlockX() + 2; x += 2) {
            for (int z = location.getBlockZ() - 2; z <= location.getBlockZ() + 2; z += 2) {
                Location loc = new Location(location.getWorld(), x, location.getBlockY(), z);

                // Skip the location itself
                if (!loc.equals(location) && bombs.containsKey(loc)) {
                    nearbyBombs.add(bombs.get(loc));
                }
            }
        }

        return nearbyBombs;
    }

    public void reset() {
        bombs.clear();
        gameElements.clear();

        GameWorld.getInstance().deleteGameWorld();
        GameWorld.getInstance().createWorld();

        setupFirstReveal(GameWorld.getInstance().getWorld().getSpawnLocation());

        // tp all players to the new world
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(GameWorld.getInstance().getWorld().getSpawnLocation());
        }

        playerLives.replaceAll((u, v) -> lives);
        currentServerLives = serverLives;

        for (UUID uuid : new ArrayList<>(timeouts.keySet())) {
            timeouts.remove(uuid);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                setGamemode(player);
            }
        }
    }

    public static void setGamemode(Player player) {
        player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        player.getInventory().addItem(Flag.getFlagStack());
    }

    private void timeoutTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            long currentTime = System.currentTimeMillis();
            for (UUID uuid : timeouts.keySet()) {
                long timeoutTime = timeouts.get(uuid);
                if (currentTime >= timeoutTime) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        playerLives.put(player.getUniqueId(), lives);
                        setGamemode(player);
                        player.teleport(GameWorld.getInstance().getWorld().getSpawnLocation());
                    }
                    timeouts.remove(uuid);
                } else {
                    long timeLeft = (timeoutTime - currentTime) / 1000;
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        PacketUtils.sendActionBar(player, "§cDu er ude i " + timeLeft + " sekunder");
                    }
                }
            }
        }, 0, 20);
    }

    public void setupFirstReveal(Location location) {
        List<Bomb> nearbyBombs = new ArrayList<>();

        nearbyBombs.addAll(getNearbyBombs(location));
        nearbyBombs.addAll(getNearbyBombs(location.clone().add(2, 0, 0)));
        nearbyBombs.addAll(getNearbyBombs(location.clone().add(-2, 0, 0)));
        nearbyBombs.addAll(getNearbyBombs(location.clone().add(0, 0, 2)));
        nearbyBombs.addAll(getNearbyBombs(location.clone().add(0, 0, -2)));

        // cant be bombs nearby the first reveal location
        if (!nearbyBombs.isEmpty()) {
            nearbyBombs.forEach(bomb -> bombs.remove(bomb.getFirstLocation()));
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
            if (element instanceof Placeholder) {
                element = new Blank(loc);
                gameElements.put(loc, element);
            }
            revealElementWithoutFilling(element);
        }
        for (Location loc : unrevealed) {
            placePlaceholderElementAt(loc);
        }
    }

    private void floodFill(Location loc, Set<Location> toFill, Set<Location> unrevealed, Set<Location> visited) {
        // Stop if the location has already been visited, revealed, or is out of bounds
        if (visited.contains(loc) ||
            bombs.containsKey(loc) ||
            toFill.contains(loc) ||
            unrevealed.contains(loc)
        ) {
            return;
        }

        // Ensure the location is within game bounds
        if (loc.getBlockX() % 2 != 0 || loc.getBlockZ() % 2 != 0 || loc.getBlockY() != 0) {
            return;
        }

        visited.add(loc);

        // Skip if the element is already a placeholder or not set by the game
        GameElement existingElement = gameElements.get(loc);
        if (existingElement != null && visited.isEmpty()) {
            if (!(existingElement instanceof Placeholder)) {
                return; // Stop the flood fill if it's not a placeholder or it's a valid element
            }
        }

        // Add the location to the fill queue
        toFill.add(loc);

        // If the element is a number, we stop filling further
        if (getElementAtLoc(loc) instanceof Number) {
            unrevealed.add(loc);
            return;
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
        for (BlockFace face : FILL_FACES) {
            Vector vector = new Vector(face.getModX(), face.getModY(), face.getModZ()).multiply(2);
            Location adjacentLoc = loc.clone().add(vector);

            if (gameElements.containsKey(adjacentLoc)) {
                continue;
            }
            Placeholder placeholder = new Placeholder(adjacentLoc);
            gameElements.put(adjacentLoc, placeholder);
            placeholder.reveal();
        }
    }

    public Map<UUID, Long> getTimeouts() {
        return timeouts;
    }
}
