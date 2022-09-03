package net.cayoe.utils.command;

import net.cayoe.BukkitBootstrap;
import net.cayoe.modules.BetterCommandsModule;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.plugin.SimplePluginManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class ServerCommandRegister {

    @Nullable
    public CommandMap getCommandMapInstance() {
        if ( Bukkit.getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
            try {
                Field field = FieldUtils.getDeclaredField( spm.getClass(), "commandMap", true );
                return (CommandMap) field.get(spm);
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( "Can't get the Bukkit CommandMap instance." );
            }
        }
        return null;
    }

    public void registerCommand(final PluginCommand pluginCommand){
        final CommandMap commandMap = getCommandMapInstance();
        if(commandMap != null)
            commandMap.register(BukkitBootstrap.getInstance().getDescription().getName(), pluginCommand);
    }

    public void registerCommand(BetterCommandsModule.GamemodeCommand gamemodeCommand) {

    }
}
