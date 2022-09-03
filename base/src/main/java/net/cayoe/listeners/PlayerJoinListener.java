package net.cayoe.listeners;

import net.cayoe.utils.player.ServerPlayer;
import net.cayoe.utils.player.SimpleServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handle(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final ServerPlayer serverPlayer = new SimpleServerPlayer(player);

        event.setJoinMessage(null);
    }
}
