package net.cayoe.utils.player;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class SimpleServerPlayerHandler implements ServerPlayerHandler{

    private final List<ServerPlayer> serverPlayers;

    public SimpleServerPlayerHandler(){
        this.serverPlayers = Lists.newArrayList();
    }

    @Override
    public void registerPlayer(ServerPlayer serverPlayer) {
        this.serverPlayers.add(serverPlayer);
    }

    @Override
    public void unregisterPlayer(ServerPlayer serverPlayer) {
        if(getPlayer(serverPlayer.uuid()) != null)
            this.serverPlayers.remove(serverPlayer);
    }

    @Override
    public ServerPlayer getPlayer(String name) {
        for (ServerPlayer serverPlayer : serverPlayers)
            if(serverPlayer.name().equals(name))
                return serverPlayer;
        return null;
    }

    @Override
    public ServerPlayer getPlayer(UUID uuid) {
        for (ServerPlayer serverPlayer : serverPlayers)
            if(serverPlayer.uuid().equals(uuid))
                return serverPlayer;
        return null;
    }

    @Override
    public List<ServerPlayer> serverPlayers() {
        return this.serverPlayers;
    }
}
