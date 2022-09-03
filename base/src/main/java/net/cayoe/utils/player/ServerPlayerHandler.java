package net.cayoe.utils.player;

import java.util.List;
import java.util.UUID;

public interface ServerPlayerHandler {

    /**
     * Register a server player
     * @param serverPlayer the registered player
     */
    void registerPlayer(final ServerPlayer serverPlayer);

    /**
     * Unregister a server player
     * @param serverPlayer the player
     * @param serverPlayer the player
     */
    void unregisterPlayer(final ServerPlayer serverPlayer);

    /**
     * Search for a player with the name
     * @param name specific name
     * @return server player
     */
    ServerPlayer getPlayer(final String name);

    /**
     * Search for a player with the uuid
     * @param uuid specific id
     * @return server player
     */
    ServerPlayer getPlayer(final UUID uuid);

    /**
     * lists all server players
     * @return a list of server players
     */
    List<ServerPlayer> serverPlayers();

}
