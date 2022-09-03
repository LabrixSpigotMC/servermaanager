package net.cayoe;

import net.cayoe.logger.LogState;
import net.cayoe.modules.*;
import net.cayoe.utils.command.ServerCommandRegister;
import net.cayoe.utils.menu.MenuDriver;
import net.cayoe.utils.menu.MenuManager;
import net.cayoe.utils.module.ModuleHandler;
import net.cayoe.utils.module.SimpleModuleHandler;
import net.cayoe.utils.player.ServerPlayerHandler;
import net.cayoe.utils.player.SimpleServerPlayerHandler;

import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;

public class Base {

    private static ModuleHandler moduleHandler;
    private static ServerPlayerHandler serverPlayerHandler;
    private static MenuDriver menuDriver;

    private static ServerCommandRegister serverCommandRegister;

    public static final String PREFIX = "§7§l┃ §6§lSERVER §8§l»§r ";

    public Base(){
        init();
        registerModules();

        new MenuManager();
    }

    private void init(){
        moduleHandler = new SimpleModuleHandler();
        serverPlayerHandler = new SimpleServerPlayerHandler();
        menuDriver = new MenuDriver();

        serverCommandRegister = new ServerCommandRegister();

    }

    private void registerModules(){
        getModuleHandler().registerModule(new ServerModule());
        getModuleHandler().registerModule(new ChatControlModule());
        getModuleHandler().registerModule(new WorldControlModule());
        getModuleHandler().registerModule(new BotCaptchaModule());
        getModuleHandler().registerModule(new MaintenanceModule());
        getModuleHandler().registerModule(new BetterCommandsModule());
        getModuleHandler().registerModule(new PlayerModule());

        //TODO:☁  in development    getModuleHandler().registerModule(new EventsModule());
        //TODO:☁  in development    getModuleHandler().registerModule(new RealisticWeatherModule());
    }

    public static void registerEvents(final Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, BukkitBootstrap.getInstance());
    }

    public static void registerCommand(final String commandName, final CommandExecutor commandExecutor){
        BukkitBootstrap.getInstance().getCommand(commandName).setExecutor(commandExecutor);
    }

    public static PluginCommand getCommand(final String name){
        return BukkitBootstrap.getInstance().getCommand(name);
    }

    public static void registerCommandWithOutYML(String fallback, BukkitCommand command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(fallback, command);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(){
        BukkitBootstrap.getInstance().saveConfig();
    }

    public static FileConfiguration getConfig(){
        return BukkitBootstrap.getInstance().getConfig();
    }

    public static void logMessage(final Player player, final LogState logState, String message){
        switch (logState){
            case INFO:
                player.sendMessage(PREFIX + "§7" + message);
                break;
            case ERROR:
                player.sendMessage(PREFIX + "§4" + message);
                break;
            case WARNING:
                player.sendMessage(PREFIX + "§c" + message);
        }
    }

    public static MenuDriver getMenuDriver() {
        return menuDriver;
    }

    public static ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public static ServerPlayerHandler getServerPlayerHandler() {
        return serverPlayerHandler;
    }

    public static ServerCommandRegister getServerCommandRegister() {
        return serverCommandRegister;
    }
}
