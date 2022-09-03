package net.cayoe.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.cayoe.Base;
import net.cayoe.BukkitBootstrap;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.*;

import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BetterCommandsModule extends Module {

    private File file;
    private static YamlConfiguration configuration;

    private static List<BetterCommand> betterCommandList;
    private List<String> betterCommandStrings;

    private HashMap<UUID, BetterCommand> playersWhoChangePermissionSub;

    @Override
    public void onLoad() {
        this.file = new File("plugins/ServerManager/better_commands.yml");
        configuration = YamlConfiguration.loadConfiguration(file);

        betterCommandList = Lists.newArrayList();
        this.betterCommandStrings = Lists.newArrayList();

        this.playersWhoChangePermissionSub = Maps.newHashMap();

        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new PlayerChatListener());

           registerConfig();

        registerCommands();

        saveConfig();
    }

    private void registerConfig(){
        editConfig("Gamemode", "betterCommands.gamemode", false);
        if(isCommandActive("Gamemode"))
            Base.registerCommandWithOutYML("gamemode", new GamemodeCommand());

        editConfig("Plugins", "betterCommands.plugins", false);
        if(isCommandActive("Plugins"))
            Base.registerCommandWithOutYML("plugins", new PluginsCommand());

        editConfig("Version", "betterCommands.version", false);
        if(isCommandActive("Version"))
            Base.registerCommandWithOutYML("version", new VersionCommand());

        editConfig("Reload", "betterCommands.reload", false);
        if(isCommandActive("Reload"))
            Base.registerCommandWithOutYML("reload", new ReloadCommand());

        editConfig("Kill", "betterCommands.kill", false);
        if(isCommandActive("Kill")) {
            Base.registerCommandWithOutYML("kill", new KillCommand());

        }
        editConfig("List", "betterCommands.list", false);
        if(isCommandActive("List"))
            Base.registerCommandWithOutYML("list", new ListCommand());

        editConfig("Time", "betterCommands.time", false);
        if(isCommandActive("Time"))
            Base.registerCommandWithOutYML("time", new TimeCommand());

        editConfig("Weather", "betterCommands.weather", false);
        if(isCommandActive("Weather"))
            Base.registerCommandWithOutYML("weather", new WeatherCommand());

        /*
        editConfig("Whitelist", "betterCommands.whitelist", false);

        editConfig("Xp", "betterCommands.xp", false);
        editConfig("Playsound", "betterCommands.playsound", false);
        editConfig("Ping", "betterCommands.ping", false);
        editConfig("Heal", "betterCommands.heal", false);
        editConfig("Feed", "betterCommands.feed", false);
        editConfig("Getposition", "betterCommands.getposition", false);
        editConfig("Suicide", "betterCommands.suicide", false);
        editConfig("Lighting", "betterCommands.lighting", false);
        editConfig("Workbench", "betterCommands.workbench", false);
        editConfig("Sudo", "betterCommands.sudo", false);
        editConfig("Itemname", "betterCommands.itemname", false);
         */
    }

    private void registerCommands(){
        for (String commandString : betterCommandStrings)
            betterCommandList.add(new BetterCommand(commandString, configuration.getString("better-commands." + commandString + ".permission")));
    }

    private void saveConfig(){
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editConfig(final String name, final String permission, final Boolean active){
        if(configuration.get("better-commands." + name + ".permission") == null)
            configuration.set("better-commands." + name + ".permission", permission);
        if(configuration.get("better-commands." + name + ".active") == null)
            configuration.set("better-commands." + name + ".active", active);

        betterCommandStrings.add(name);
        configuration.set("betterCommandStrings", betterCommandStrings);

        saveConfig();
    }

    @Override
    public void openInventory(Player player) {
        new ScrollableMenu("§7§l┃ §6BetterCommands", 9*6, 9, 9*4, 9*5+8, 9*5 + 1, 9*5,
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76", "§7§l✖ §8┃ §cNo more pages found.").addLore("§8§oThere are no other pages.").build(),
                new ItemBuilder(Material.REDSTONE).build(),
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be", "§7§l» §8┃ §eNext page").build(),
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d", "§7§l« §8┃ §ePage back").build()
        ).open(player, menuContainer -> {
            for (int i = 0; i < 9; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            for (int i = 9*4; i < 9*5; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +2, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +7, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

            menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullTexture("438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                    .addLore("§8› §7BetterCommands §8‹")
                    .build());

            menuContainer.setItem(9*5 + 3, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());
            menuContainer.setItem(9*5 + 4, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§8┃ §a§oEnable all").build());
            menuContainer.setItem(9*5 + 5, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§8┃ §c§oDisable all").build());
            menuContainer.setItem(9*5 + 6, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());

            for (BetterCommand betterCommand : betterCommandList)
                menuContainer.addSortItem(new ItemBuilder(Material.PAPER)
                        .setDisplayName("§8» §6" + betterCommand.getName())
                        .addLore(" ", "§7Permission: §e§o" + betterCommand.getPermission(), "§7Active: §e§o" + betterCommand.isActive(betterCommand.getName()), " ", "§e§o⚡ Click to edit ⚡").build());
        });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String name() {
        return "§8│ §6BetterCommands";
    }

    @Override
    public String realName() {
        return "better_commands";
    }

    @Override
    public String description() {
        return "Activate new or reloaded Command";
    }

    @Override
    public String permission() {
        return "server-manager.modules.better_commands";
    }

    @Override
    public Boolean needPermission() {
        return false;
    }

    @Override
    public Boolean hasSkullID() {
        return true;
    }

    @Override
    public String getSkullID() {
        return "7e1f5c0350100d55f95d173ae9b84882a5026c095d88cca5f9b8e893562a06cf";
    }

    private void openSubMenu(final Player player, final BetterCommand betterCommand){
        new Menu("§r§8§l» §6BetterCommand", 9, 8, new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build())
                .open(player, menuContainer -> {
                    menuContainer.addItem(new ItemBuilder(Material.PAPER)
                            .setDisplayName("§8» §6" + betterCommand.getName())
                            .addLore(" ", "§7Permission: §e§o" + betterCommand.getPermission(), "§7Active: §e§o" + betterCommand.isActive(betterCommand.getName()), " ", "§e§o⚡ Click to edit ⚡").build());
                    menuContainer.addItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());

                    if(!betterCommand.isActive(betterCommand.getName()))
                        menuContainer.addItem(new ItemBuilder(Material.LIME_DYE).setDisplayName("§8§l» §aEnable Command").build());
                    else
                        menuContainer.addItem(new ItemBuilder(Material.GRAY_DYE).setDisplayName("§8§l» §cDisable Command").build());

                    menuContainer.addItem(new ItemBuilder(Material.WITHER_ROSE).setDisplayName("§8§l» §eChange permission")
                            .addLore(" ", "§a§o" + betterCommand.getPermission(), " ", "§7§oClick to edit").build());
                });
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(event.getView().getTitle().equals("§7§l┃ §6BetterCommands")){
                event.setCancelled(true);

                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8┃ §a§oEnable all")){
                    for (BetterCommand betterCommand : betterCommandList) {
                        configuration.set("better-commands." + betterCommand.getName() + ".active", true);
                        saveConfig();
                    }
                    openInventory(player);
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8┃ §c§oDisable all")){
                    for (BetterCommand betterCommand : betterCommandList) {
                        configuration.set("better-commands." + betterCommand.getName() + ".active", false);
                        saveConfig();
                    }
                    openInventory(player);
                    return;
                }

                if(event.getCurrentItem().getType().equals(Material.PAPER)){
                    final String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§8» §6", "");

                    openSubMenu(player, getCommand(name));
                }
                return;
            }
            if(event.getView().getTitle().equals("§r§8§l» §6BetterCommand")){
                final BetterCommand betterCommand = getCommand(Objects.requireNonNull(event.getView().getItem(0).getItemMeta()).getDisplayName().replaceAll("§8» §6", ""));

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §aEnable Command")) {
                    configuration.set("better-commands." + betterCommand.getName() + ".active", true);
                    saveConfig();

                    player.sendMessage("§e§oRestart the server to apply.");
                    player.closeInventory();
                }else if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §cDisable Command")){
                    configuration.set("better-commands." + Objects.requireNonNull(betterCommand).getName() + ".active", false);
                    saveConfig();

                    player.sendMessage("§e§oRestart the server to apply.");
                    player.closeInventory();
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §eChange permission")){
                    playersWhoChangePermissionSub.put(player.getUniqueId(), betterCommand);

                    player.closeInventory();
                    player.sendMessage(Base.PREFIX + "§7Write the new name of the permission in the chat. ");
                    player.sendMessage(Base.PREFIX + "§7Write §c\"cancel\"§7 in the chat to cancel the process");
                }

                openSubMenu(player, betterCommand);
            }
        }
    }

    public class PlayerChatListener implements Listener{

        @EventHandler
        public void handle(final PlayerChatEvent event){
            final Player player = event.getPlayer();

            if(playersWhoChangePermissionSub.containsKey(player.getUniqueId())){
                if(event.getMessage().equalsIgnoreCase("cancel")){
                    openSubMenu(player, playersWhoChangePermissionSub.get(player.getUniqueId()));
                    playersWhoChangePermissionSub.remove(player.getUniqueId());
                    return;
                }
                final BetterCommand betterCommand = playersWhoChangePermissionSub.get(player.getUniqueId());

                configuration.set("better-commands." + betterCommand.getName() + ".permission", event.getMessage());
                saveConfig();

                openSubMenu(player, playersWhoChangePermissionSub.get(player.getUniqueId()));
                playersWhoChangePermissionSub.remove(player.getUniqueId());

                player.closeInventory();
                saveConfig();
            }
        }
    }

    private static BetterCommand getCommand(final String name){
        for (BetterCommand command : betterCommandList) {
            if(command.getName().equals(name))
                return command;
        }
        return null;
    }

    public class BetterCommand {

        private final String name;
        private final String permission;

        public BetterCommand(String name, String permission) {
            this.name = name;
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }

        public String getName() {
            return name;
        }

        public Boolean isActive(final String name){
            return configuration.getBoolean("better-commands." + name + ".active");
        }
    }

    public Boolean isCommandActive(final String name){
        return configuration.getBoolean("better-commands." + name + ".active");
    }

    public class GamemodeCommand extends BukkitCommand {

        public GamemodeCommand(){
            super("Gamemode");
            setAliases(Arrays.asList("gm"));
            setDescription("Change your game mode");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Gamemode").getPermission())){
                    switch (args.length){
                        case 0:
                            player.sendMessage("§4Wrong entry: §7§o/gamemode <0-3> <user>");
                            break;
                        case 1:
                            if(args[0].equalsIgnoreCase("SURVIVAL")
                                    || args[0].equalsIgnoreCase("0")){
                                player.setGameMode(GameMode.SURVIVAL);
                                player.sendMessage("§7§oYou have changed your game mode: §e" + player.getGameMode().name());
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("CREATIVE")
                                    || args[0].equalsIgnoreCase("1")){
                                player.setGameMode(GameMode.CREATIVE);
                                player.sendMessage("§7§oYou have changed your game mode: §e" + player.getGameMode().name());
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("ADVENTURE")
                                    || args[0].equalsIgnoreCase("2")){
                                player.setGameMode(GameMode.ADVENTURE);
                                player.sendMessage("§7§oYou have changed your game mode: §e" + player.getGameMode().name());
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("SPECTATOR")
                                    || args[0].equalsIgnoreCase("3")){
                                player.setGameMode(GameMode.SPECTATOR);
                                player.sendMessage("§7§oYou have changed your game mode: §e" + player.getGameMode().name());
                                return true;
                            }
                            break;
                        case 2:
                            final String targetName = args[2];

                            if(Bukkit.getPlayer(targetName) != null){
                                final Player targetPlayer = Bukkit.getPlayer(targetName);
                                if(args[0].equalsIgnoreCase("SURVIVAL")
                                        || args[0].equalsIgnoreCase("0")){
                                    targetPlayer.setGameMode(GameMode.SURVIVAL);
                                    player.sendMessage("§7§oYou have changed the game mode from " + targetPlayer.getName() + ": §e" + player.getGameMode().name());
                                    return true;
                                }
                                if(args[0].equalsIgnoreCase("CREATIVE")
                                        || args[0].equalsIgnoreCase("1")){
                                    targetPlayer.setGameMode(GameMode.CREATIVE);
                                    player.sendMessage("§7§oYou have changed the game mode from " + targetPlayer.getName() + ": §e" + player.getGameMode().name());
                                    return true;
                                }
                                if(args[0].equalsIgnoreCase("ADVENTURE")
                                        || args[0].equalsIgnoreCase("2")){
                                    targetPlayer.setGameMode(GameMode.ADVENTURE);
                                    player.sendMessage("§7§oYou have changed the game mode from " + targetPlayer.getName() + ": §e" + player.getGameMode().name());
                                    return true;
                                }
                                if(args[0].equalsIgnoreCase("SPECTATOR")
                                        || args[0].equalsIgnoreCase("3")){
                                    targetPlayer.setGameMode(GameMode.SPECTATOR);
                                    player.sendMessage("§7§oYou have changed the game mode from " + targetPlayer.getName() + ": §e" + player.getGameMode().name());
                                    return true;
                                }
                            }
                            break;
                    }
                }
            }
            return false;
        }


        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
            final List<String> completions = Lists.newArrayList();

            switch (args.length){
                case 0:
                    completions.add("SURVIVAL");
                    completions.add("CREATIVE");
                    completions.add("ADVENTURE");
                    completions.add("SPECTATOR");
                    break;
                case 1:
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                        completions.add(onlinePlayer.getName());
                    break;
            }

            return completions;
        }
    }

    public class VersionCommand extends BukkitCommand {

        protected VersionCommand() {
            super("version");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Version").getPermission()))
                    player.sendMessage("§a§oThis server is running " + Bukkit.getBukkitVersion() + " with max " + (Runtime.getRuntime().maxMemory() / 1048576L) + "mb");

            }
            return false;
        }
    }

    public class PluginsCommand extends BukkitCommand {

        protected PluginsCommand() {
            super("plugins");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Plugins").getPermission())) {
                    final StringBuilder stringBuilder = new StringBuilder("§7Plugins §8(§7" + Bukkit.getPluginManager().getPlugins().length + "§8)§7: §e");

                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
                        stringBuilder.append(plugin.getName() + ", ");

                    player.sendMessage(stringBuilder.toString());
                }
            }
            return false;
        }
    }

    public class ReloadCommand extends BukkitCommand {

        protected ReloadCommand() {
            super("reload");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Reload").getPermission())) {
                    player.sendMessage("§e§oTry reloading the server...");
                    Bukkit.reload();
                    player.sendMessage("§a§oReload successful...");
                }
            }
            return false;
        }
    }

    public class KillCommand extends BukkitCommand {

        protected KillCommand() {
            super("kill");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Kill").getPermission())) {
                    switch (args.length){
                        case 0:
                            player.sendMessage("§4Wrong entry: §7§o/kill <user>");
                            break;
                        case 1:
                            final String targetName = args[1];

                            if(Bukkit.getPlayer(targetName) != null){
                                final Player targetPlayer = Bukkit.getPlayer(targetName);
                                targetPlayer.setHealth(0);

                                player.sendMessage("§7§oYou have killed the following player: §e§o" + targetPlayer.getName());
                            }
                            break;
                    }
                }
            }
            return false;
        }
    }

    public class ListCommand extends BukkitCommand {

        protected ListCommand() {
            super("list");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("List").getPermission())) {
                    switch (args.length){
                        case 0:
                            final StringBuilder stringBuilder = new StringBuilder("§7Players §8(§7" + Bukkit.getOnlinePlayers().size() + "§8)§7: §e");

                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                stringBuilder.append(onlinePlayer.getName() + ", ");
                            }

                            player.sendMessage(stringBuilder.toString());
                            break;
                    }
                }
            }
            return false;
        }
    }

    public class TimeCommand extends BukkitCommand {

        protected TimeCommand() {
            super("time");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Time").getPermission())) {
                    switch (args.length){
                        case 0:
                            player.sendMessage("§4Wrong entry: §7§o/time <night/day/midnight/noon>, /time set <ticks>");
                            break;
                        case 1:
                            if(args[0].equalsIgnoreCase("night")){
                                player.sendMessage("§7§oYou have changed the time: §e§oNIGHT");
                                player.getWorld().setTime(13000);
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("midnight")){
                                player.sendMessage("§7§oYou have changed the time: §e§oMIDNIGHT");
                                player.getWorld().setTime(18000);
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("noon")){
                                player.sendMessage("§7§oYou have changed the time: §e§oNOON");
                                player.getWorld().setTime(6000);
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("day")){
                                player.sendMessage("§7§oYou have changed the time: §e§oDAY");
                                player.getWorld().setTime(1000);
                                return true;
                            }
                            break;
                        case 2:
                            if(args[0].equalsIgnoreCase("set")){
                                try {
                                    final int time = Integer.parseInt(args[1]);
                                    player.getWorld().setTime(time);
                                    player.sendMessage("§7§oYou have changed the time: §e§o" + time + "ticks");
                                }catch (NumberFormatException exception) {}
                                return true;
                            }
                            break;
                    }
                }
            }
            return false;
        }
    }

    public class WeatherCommand extends BukkitCommand {

        protected WeatherCommand() {
            super("weather");
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] args) {
            if(commandSender instanceof Player){
                final Player player = (Player) commandSender;

                if(player.hasPermission(getCommand("Weather").getPermission())) {
                    switch (args.length){
                        case 0:
                            player.sendMessage("§4Wrong entry: §7§o/weather <clear/rain/thunder>");
                            break;
                        case 1:
                            if(args[0].equalsIgnoreCase("clear")){
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.getWorld().setThundering(false);
                                    onlinePlayer.getWorld().setStorm(false);
                                }

                                player.sendMessage("§7§oYou have changed the weather: §e§oCLEAR");
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("rain")){
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.getWorld().setStorm(true);
                                }

                                player.sendMessage("§7§oYou have changed the weather: §e§oRAIN");
                                return true;
                            }
                            if(args[0].equalsIgnoreCase("thunder")){
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.getWorld().setThundering(true);
                                    onlinePlayer.getWorld().setStorm(true);
                                }

                                player.sendMessage("§7§oYou have changed the weather: §e§oTHUNDER");
                            }
                            break;
                    }
                }
            }
            return false;
        }
    }


}
