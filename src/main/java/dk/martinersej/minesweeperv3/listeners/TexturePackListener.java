package dk.martinersej.minesweeperv3.listeners;

import dk.martinersej.minesweeperv3.Minesweeper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class TexturePackListener implements Listener {

    public static final String RESOURCE_PACK_URL = "https://drive.google.com/uc?export=download&id=1tiJaJ-qhBkBLb3h3t7eY4BDm8_8aUfHl";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Minesweeper.get(), () -> {
            event.getPlayer().sendMessage("§eVi anbefaler at du bruger vores texture pack for at få den bedste oplevelse!");
            event.getPlayer().setResourcePack(RESOURCE_PACK_URL);
        }, 10L);
    }

//    @EventHandler
//    public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
//        if (event.getStatus() != PlayerResourcePackStatusEvent.Status.ACCEPTED) {
////            event.getPlayer().setResourcePack(RESOURCE_PACK_URL);
//        }
//    }
}
