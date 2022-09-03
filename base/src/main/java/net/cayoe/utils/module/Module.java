package net.cayoe.utils.module;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Module implements Listener {

    public void onLoad(){
    }

    public void onInventoryClick(){

    }

    public abstract void openInventory(final Player player);

    public abstract Material material();

    public abstract String name();
    public abstract String realName();
    public abstract String description();
    public abstract String permission();

    public abstract Boolean needPermission();

    public Boolean hasSkullID(){
        return false;
    }

    public String getSkullID(){
        return null;
    }



}
