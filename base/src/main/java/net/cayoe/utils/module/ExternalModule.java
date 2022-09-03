package net.cayoe.utils.module;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class ExternalModule extends Module {

    @Override
    public void onLoad(){}

    @Override
    public void onInventoryClick(){}

    @Override
    public abstract void openInventory(final Player player);

    @Override
    public abstract Material material();

    @Override
    public abstract String name();
    @Override
    public abstract String realName();
    @Override
    public abstract String description();
    @Override
    public abstract String permission();

    public abstract String creditsURL();
    public abstract String creditsAuthor();

    public abstract String pluginName();
    public abstract String pluginAuthor();

    @Override
    public abstract Boolean needPermission();

    public String extraText(){
        return null;
    }

    @Override
    public Boolean hasSkullID(){
        return false;
    }

    @Override
    public String getSkullID(){
        return null;
    }

}
