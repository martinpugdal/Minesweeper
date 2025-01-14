package dk.martinersej.minesweeperv3.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtils {

    public static void sendTitle(Player player, String title, String subTitle) {
        sendTitle(player, title, subTitle, 10, 70, 20);
    }

    public static void sendTitle(Player player, String title, String subTitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
        IChatBaseComponent subtitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle + "\"}");

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent, fadeInTicks, durationTicks, fadeOutTicks);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent, fadeInTicks, durationTicks, fadeOutTicks);

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(subtitlePacket);
        playerConnection.sendPacket(titlePacket);

        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInTicks, durationTicks, fadeOutTicks);
        playerConnection.sendPacket(length);

    }

    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatComponent, (byte) 2);

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.playerConnection.sendPacket(packetPlayOutChat);
    }
}