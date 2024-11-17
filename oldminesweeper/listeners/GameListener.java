package dk.martinersej.oldminesweeper.listeners;

import dk.martinersej.oldminesweeper.GameElement;
import dk.martinersej.oldminesweeper.GameWorld;
import dk.martinersej.oldminesweeper.Minesweeper;
import dk.martinersej.oldminesweeper.elements.Bomb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GameListener implements Listener {

    boolean firstJoin = false;

    public GameListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!firstJoin) {
            firstJoin = true;
            Minesweeper.get().reset();
        }
        // teleport after 1 tick because of invisible bug where the player is invisible
        Bukkit.getScheduler().runTaskLater(Minesweeper.get(), () -> {
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
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getLocation().getWorld() != GameWorld.getInstance().getWorld()) {
            return;
        }

        Location location = event.getClickedBlock().getLocation();
        event.setCancelled(true);

        // if held item is flag item
        if (event.getPlayer().getItemInHand().getType() == Material.BLAZE_POWDER) {
            Minesweeper.get().placeFlag(location);
        } else {
            GameElement element = Minesweeper.get().getElementAtLoc(location);

            if (Minesweeper.get().isRevealed(location)) {
                return;
            }

            Minesweeper.get().revealElement(element);
            if (element instanceof Bomb) {
                Bukkit.broadcastMessage("§e" + event.getPlayer().getName() + " fik jer alle til at dø");
            }
        }
    }
}
