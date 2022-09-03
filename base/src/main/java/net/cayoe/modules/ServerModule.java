package net.cayoe.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.cayoe.Base;
import net.cayoe.BukkitBootstrap;
import net.cayoe.logger.LogState;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.inventory.DefaultInventory;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.*;

public class ServerModule extends Module {

    private static String currentMessage;

    private static List<UUID> motdChangerPlayers = Lists.newArrayList();

    @Override
    public void onLoad() {
        currentMessage = "§cNo text set";

        if(Base.getConfig().get("servercontrol.max_players") == null)
            Base.getConfig().set("servercontrol.max_players", Bukkit.getServer().getMaxPlayers());
        if(Base.getConfig().get("servercontrol.motd") == null)
            Base.getConfig().set("servercontrol.motd", Bukkit.getMotd());

        Base.saveConfig();

        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new ServerListPingListener());
        Base.registerEvents(new WeatherChangeListener());
        Base.registerEvents(new PlayerChatListener());
        Base.registerEvents(new Broadcast.GeneralListener());
    } 

    @Override
    public void openInventory(final Player player) {
        Base.getMenuDriver().createMenu("§7§l┃ §c§lSERVERCONTROL", 9*6, 53,new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build(),  menu -> {
            menu.open(player, menuContainer -> {
                for (int i = 0; i < 9; i++)
                    menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                for (int i = 43; i < 53; i++)
                    menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                menuContainer.setItem(4, new ItemBuilder(Material.NETHERITE_SCRAP)
                        .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                        .addLore("§8› §7ServerControl menu §8‹")
                        .build());

                menuContainer.setItem(19, new ItemBuilder(Material.PLAYER_HEAD)
                        .setSkullTexture("8ae7bf4522b03dfcc866513363eaa9046fddfd4aa6f1f0889f03c1e6216e0ea0", "§6Broadcast Message").build());

                if(Base.getConfig().getBoolean("servercontrol.weather_change"))
                    menuContainer.setItem(20, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("b4d7651e9deb5c2813820fed311d541119f3155eaab549d3ad52d202bc3f0e7", "§6Weather: Automatic change")
                            .addLore("§8› §aActive §8‹").build());
                else
                    menuContainer.setItem(20, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("b4d7651e9deb5c2813820fed311d541119f3155eaab549d3ad52d202bc3f0e7", "§6Weather: Automatic change")
                            .addLore("§8› §cDeactivate §8‹").build());

                if(Bukkit.getServer().hasWhitelist())
                    menuContainer.setItem(21, new ItemBuilder(Material.GOLDEN_SWORD, "§6Whitelist")
                            .addLore("§8› §aActive §8‹").build());
                else
                    menuContainer.setItem(21, new ItemBuilder(Material.GOLDEN_SWORD, "§6Whitelist")
                            .addLore("§8› §cDisabled §8‹").build());

                menuContainer.setItem(22, new ItemBuilder(Material.ITEM_FRAME, "§6Max players")
                        .addLore("§8› §7" + Base.getConfig().getInt("servercontrol.max_players") + " §8‹").build());
                menuContainer.setItem(23, new ItemBuilder(Material.NAME_TAG, "§6Motd")
                        .addLore("§8› §7" + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Base.getConfig().getString("servercontrol.motd")))+ " §8‹").build());
                menuContainer.setItem(24, new ItemBuilder(Material.ZOMBIE_HEAD, "§6Difficulty")
                        .addLore("§8› §7" + player.getWorld().getDifficulty().toString() + " §8‹").build());

                final Runtime runtime = Runtime.getRuntime();

                menuContainer.setItem(25, new ItemBuilder(Material.SPECTRAL_ARROW, "§6Current memory")
                        .addLore(" ",
                                "§e§oUsed memory",
                                "§8› §7" + ((runtime.totalMemory() - runtime.freeMemory()) / 1048576L) + "MB §8‹",
                                " ",
                                "§e§oFree memory",
                                "§8› §7" + (runtime.maxMemory() / 1048576L - (runtime.totalMemory() - runtime.freeMemory()) / 1048576L) + "MB §8‹",
                                " ",
                                "§e§oMax memory",
                                "§8› §7" + (runtime.maxMemory() / 1048576L) + "MB §8‹",
                                " ",
                                "§e§o⚡ Click to refresh ⚡").build());

                /*
                if(player.getWorld().getGameRuleDefault(GameRule.ANNOUNCE_ADVANCEMENTS))
                    menuContainer.setItem(25, new ItemBuilder(Material.GOLD_INGOT, "§6Player achievements")
                            .addLore("§8› §aActive §8‹").build());
                else
                    menuContainer.setItem(25, new ItemBuilder(Material.GOLD_INGOT, "§6Player achievements")
                            .addLore("§8› §cDeactivate §8‹").build());
                 **/
            });


        });
    }

    @Override
    public Material material() {
        return Material.NETHERITE_SCRAP;
    }

    @Override
    public String realName() {
        return "server_control";
    }

    @Override
    public String name() {
        return "§8│ §aServerControl";
    }

    @Override
    public String description() {
        return "Monitor the Minecraft server or change settings for the server.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.server";
    }

    @Override
    public Boolean needPermission() {
        return true;
    }

    @Override
    public Boolean hasSkullID() {
        return false;
    }

    @Override
    public String getSkullID() {
        return null;
    }

    public static void setCurrentMessage(String currentMessage) {
        ServerModule.currentMessage = currentMessage;
    }

    public static String getCurrentMessage() {
        return currentMessage;
    }

    public static class PlayerChatListener implements Listener {

        @EventHandler
        public void handle(final PlayerChatEvent event){
            final Player player = event.getPlayer();

            if(motdChangerPlayers.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    motdChangerPlayers.remove(player.getUniqueId());
                    Base.getModuleHandler().getModule("server_control").openInventory(player);
                    return;
                }

                motdChangerPlayers.remove(player.getUniqueId());

                Base.getConfig().set("servercontrol.motd", event.getMessage());
                Base.saveConfig();

                player.sendMessage(Base.PREFIX + "§aThe motd was successfully changed.");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Base.getConfig().getString("servercontrol.motd"))));

            }
        }

    }

    public static class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if (event.getView().getTitle().equals("§7§l┃ §c§lSERVERCONTROL")) {
                event.setCancelled(true);

                if(Base.getModuleHandler().getModule("server_control").needPermission())
                    if(!player.hasPermission(Base.getModuleHandler().getModule("server_control").permission()))
                        return;

                if(!player.hasPermission(Base.getModuleHandler().getModule("server_control").permission())){
                    Base.logMessage(player, LogState.WARNING, "You do not have the required permissions!");
                    return;
                }

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§aBack":
                        new DefaultInventory().openInventory(player);
                        break;
                    case "§6Broadcast Message":
                        new Broadcast(player).inventory();
                        break;
                    case "§6Weather: Automatic change":
                        if(Base.getConfig().getBoolean("servercontrol.weather_change"))
                            Base.getConfig().set("servercontrol.weather_change", false);
                        else
                            Base.getConfig().set("servercontrol.weather_change", true);

                        Base.saveConfig();
                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                    case "§6Whitelist":
                        if(Bukkit.getServer().hasWhitelist())
                            Bukkit.getServer().setWhitelist(false);
                        else
                            Bukkit.getServer().setWhitelist(true);
                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                    case "§6Max players":
                        final int currentMaxPlayers = Base.getConfig().getInt("servercontrol.max_players");

                        if(event.isLeftClick())
                            Base.getConfig().set("servercontrol.max_players", (currentMaxPlayers - 1));
                        else
                            Base.getConfig().set("servercontrol.max_players", (currentMaxPlayers + 1));

                        Base.saveConfig();
                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                    case "§6Difficulty":
                        for (World world : Bukkit.getWorlds()) {
                            if(world.getDifficulty().equals(Difficulty.PEACEFUL))
                                world.setDifficulty(Difficulty.EASY);
                            else if(world.getDifficulty().equals(Difficulty.EASY))
                                world.setDifficulty(Difficulty.NORMAL);
                            else if(world.getDifficulty().equals(Difficulty.NORMAL))
                                world.setDifficulty(Difficulty.HARD);
                            else if(world.getDifficulty().equals(Difficulty.HARD))
                                world.setDifficulty(Difficulty.PEACEFUL);
                        }
                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                    case "§6Motd":
                        motdChangerPlayers.add(player.getUniqueId());
                        player.closeInventory();

                        player.sendMessage(Base.PREFIX + "§7Write the new motd in the chat. ");
                        player.sendMessage(Base.PREFIX + "§7If you want to start a new line write §e\"\\n\"§7.");
                        player.sendMessage(Base.PREFIX + "§7If you want to cancel the process, write §c\"cancel\"§7.");

                        break;
                    case "§6Current memory":
                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                        /*
                    case "§6Player achievements":
                        for (World world : Bukkit.getWorlds()) {
                            if(world.getGameRuleDefault(GameRule.ANNOUNCE_ADVANCEMENTS).booleanValue() == true)
                                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                            else if(world.getGameRuleDefault(GameRule.ANNOUNCE_ADVANCEMENTS).booleanValue() == false)
                                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
                        }

                        Base.getModuleHandler().getModule("server_control").openInventory(player);
                        break;
                         **/
                }
                return;
            }
        }

    }

    public static class ServerListPingListener implements Listener {

        @EventHandler
        public void handle(final ServerListPingEvent event){

            event.setMaxPlayers(Base.getConfig().getInt("servercontrol.max_players"));
            event.setMotd(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Base.getConfig().getString("servercontrol.motd"))));

        }

    }

    public static class WeatherChangeListener implements Listener {

        @EventHandler
        public void handle(final WeatherChangeEvent event){
            if(!Base.getConfig().getBoolean("servercontrol.weather_change"))
                event.setCancelled(true);
        }

    }

    public static class Broadcast {

        private final Player player;

        public static final List<UUID> CHANGE_MESSAGE = Lists.newArrayList();
        public static final Map<UUID, Broadcast> SET_UP = Maps.newHashMap();

        public Broadcast(Player player){
            this.player = player;
        }

        public void inventory(){
            Base.getMenuDriver().createMenu("§7§l┃ §6§lBROADCAST", 9, menu -> {
                menu.open(player, menuContainer -> {
                    menuContainer.addItem(new ItemBuilder(Material.BOOK, "§7§l┃ §6§lBROADCAST")
                            .addLore("§8› §r" + ServerModule.getCurrentMessage() +  " §8‹")
                            .build());
                    menuContainer.addItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "  ").build());


                    menuContainer.addItem(new ItemBuilder(Material.BLACK_DYE).setDisplayName("§eChange message").build());


                    menuContainer.addItem(new ItemBuilder(Material.GREEN_DYE).setDisplayName("§eSend broadcast").build());

                    menuContainer.setItem(8, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9", "§aBack").build());
                });
            });
        }

        public Player getPlayer() {
            return player;
        }

        public static class GeneralListener implements Listener {

            @EventHandler
            public void handleInventoryClick(final InventoryClickEvent event){
                final Player player = (Player) event.getWhoClicked();

                if(event.getCurrentItem() == null) return;
                if(event.getCurrentItem().getItemMeta() == null) return;

                if(event.getView().getTitle().equalsIgnoreCase("§7§l┃ §6§lBROADCAST")){
                    event.setCancelled(true);

                    //   if(SET_UP.containsKey(player.getUniqueId())){
                    final Broadcast broadcast = SET_UP.get(player.getUniqueId());

                    switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                        case "§7§l┃ §6§lBROADCAST":
                            new Broadcast(player).inventory();
                            break;
                        case "§eChange message":
                            player.closeInventory();

                            Base.logMessage(player, LogState.INFO, "§7Please enter the message you would like to send to everyone in the chat. To cancel, write §c\"cancel");
                            CHANGE_MESSAGE.add(player.getUniqueId());
                            break;
                        case "§eSend broadcast":
                            player.closeInventory();
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ServerModule.getCurrentMessage()));
                            break;
                        case "§aBack":
                            Base.getModuleHandler().getModule("server_control").openInventory(player);
                            break;
                    }
                    //  }
                }
            }

            @EventHandler
            public void handlePlayerChat(final PlayerChatEvent event){
                final Player eventPlayer = event.getPlayer();

                if(CHANGE_MESSAGE.contains(eventPlayer.getUniqueId())){
                    event.setCancelled(true);

                    if(event.getMessage().equalsIgnoreCase("cancel")){
                        new Broadcast(eventPlayer).inventory();
                        CHANGE_MESSAGE.remove(eventPlayer.getUniqueId());
                        return;
                    }

                    ServerModule.setCurrentMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                    new Broadcast(eventPlayer).inventory();
                    CHANGE_MESSAGE.remove(eventPlayer.getUniqueId());


                }

            }

        }

    }

}
