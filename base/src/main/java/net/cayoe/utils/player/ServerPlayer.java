package net.cayoe.utils.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface ServerPlayer {

    void join();

    void quit();

    void sendActionbar(final String message);

    Player bukkitPlayer();

    UUID uuid();

    String name();

}
