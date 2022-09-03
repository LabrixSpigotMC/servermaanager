package net.cayoe;

import net.cayoe.commands.DefaultCommand;
import net.cayoe.listeners.InventoryClickListener;
import net.cayoe.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBootstrap extends JavaPlugin {

    private static BukkitBootstrap instance;

    private Base base;

    @Override
    public void onEnable() {
        this.saveConfig();

        instance = this;

        getCommand("servermanager").setExecutor(new DefaultCommand());

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        base();


    }

    private void base(){
        this.base = new Base();
    }

    public Base getBase() {
        return base;
    }

    public static BukkitBootstrap getInstance() {
        return instance;
    }
}
