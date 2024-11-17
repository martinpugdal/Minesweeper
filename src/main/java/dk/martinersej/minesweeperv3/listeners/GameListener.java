package dk.martinersej.minesweeperv3.listeners;

import dk.martinersej.minesweeperv3.Minesweeper;
import dk.martinersej.minesweeperv3.gameelement.GameElement;
import dk.martinersej.minesweeperv3.gameelement.elements.Flag;
import dk.martinersej.minesweeperv3.utils.GameWorld;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GameListener implements Listener {

    boolean firstJoin = false;

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(event.getItemDrop().getItemStack().getType().equals(Material.BLAZE_POWDER));
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!firstJoin) {
            firstJoin = true;
            Minesweeper.get().reset();
        }
        Minesweeper.setGamemode(player);
        // teleport after 1 tick because of invisible bug where the player is invisible
        Bukkit.getScheduler().runTaskLater(Minesweeper.get(), () -> {
            boolean hasTimeout = Minesweeper.get().getTimeouts().get(player.getUniqueId()) != null;
            if (hasTimeout) {
                // ensure the gamemode is correct
                player.setGameMode(GameMode.SPECTATOR);
            }
            player.teleport(GameWorld.getInstance().getWorld().getSpawnLocation());
        }, 1L);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onElementInteract(PlayerInteractEvent event) {
        if (
            event.getClickedBlock() == null ||
                event.getClickedBlock().getLocation().getWorld() != GameWorld.getInstance().getWorld()
        ) return;

        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        Location location = event.getClickedBlock().getLocation();
        event.setCancelled(true);

        if (event.getPlayer().getItemInHand().getType().equals(Flag.getFlagStack().getType())) {
            Minesweeper.get().placeFlag(location);
            return;
        }

        GameElement element = Minesweeper.get().getElementAtLocWithoutPlaceholder(location);
        Minesweeper.get().revealElement(event.getPlayer(), element);
    }

//    @EventHandler
//    public void onWalkOverElements(PlayerMoveEvent event) {
//        Location location = event.getTo();
//        if (location == null || location.getWorld() != GameWorld.getInstance().getWorld()) return;
//
//        GameElement element = Minesweeper.get().getElementAtLocWithoutPlaceholder(location);
//
//        Minesweeper.get().revealElement(element);
//        if (element.getType() == GameElementType.BOMB) {
//            Bomb bomb = (Bomb) element;
//            if (!bomb.isRevealed()) {
//                bomb.setRevealed(true);
//                Bukkit.broadcastMessage("§e" + event.getPlayer().getName() + " fik jer alle til at dø");
//            }
//        }
//    }
}
