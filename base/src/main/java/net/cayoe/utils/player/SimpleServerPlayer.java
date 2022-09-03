package net.cayoe.utils.player;

import net.cayoe.Base;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SimpleServerPlayer implements ServerPlayer {

    private final Player player;
    private final String name;
    private final UUID uuid;

    private boolean setup;

    public SimpleServerPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();

        this.setup = false;

        this.join();
    }

    @Override
    public void join() {
        Base.getServerPlayerHandler().registerPlayer(this);


    }

    @Override
    public void quit() {


    }

    @Override
    public void sendActionbar(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @Override
    public Player bukkitPlayer() {
        return this.player;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public String name() {
        return this.name;
    }
}
