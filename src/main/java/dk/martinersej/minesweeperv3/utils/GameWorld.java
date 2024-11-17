package dk.martinersej.minesweeperv3.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;

public class GameWorld {

    private static GameWorld instance = null;

    private World world;

    public GameWorld() {
        instance = this;
    }

    public static GameWorld getInstance() {
        if (instance == null) {
            instance = new GameWorld();
        }
        return instance;
    }

    public void createWorld() {
        if (world != null || Bukkit.getWorld(getClass().getSimpleName()) != null) {
            // delete the world if it already exists
            world = null;
            deleteGameWorld();
        }

        WorldCreator worldCreator = new WorldCreator(getClass().getSimpleName());
        worldCreator.generator(new GameGenerator());
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generateStructures(false);
        this.world = worldCreator.createWorld();
        this.world.setAutoSave(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("randomTickSpeed", "0");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("doWeatherCycle", "false");
        this.world.setGameRuleValue("showDeathMessages", "false");
        this.world.setDifficulty(Difficulty.EASY);
        this.world.setSpawnLocation(0, 1, 0);
    }

    public void deleteGameWorld() {
        World gameWorld = Bukkit.getWorld(getClass().getSimpleName());

        if (gameWorld != null) {
            World world = Bukkit.getWorlds().get(0);
            for (Player player : gameWorld.getPlayers()) {
                player.teleport(world.getSpawnLocation());
            }
        }

        try {
            Bukkit.unloadWorld(getClass().getSimpleName(), false);
            this.world = null;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            System.out.println("Failed unloading the world!");
        }

        FileUtils.deleteDir(new File(getClass().getSimpleName()));
    }

    public World getWorld() {
        return world;
    }
}
