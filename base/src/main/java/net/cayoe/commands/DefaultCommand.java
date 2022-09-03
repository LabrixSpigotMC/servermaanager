package net.cayoe.commands;

import net.cayoe.Base;
import net.cayoe.logger.LogState;
import net.cayoe.utils.inventory.DefaultInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DefaultCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(Base.PREFIX + "§cYou're not a player");
            return true;
        }

        final Player player = (Player) commandSender;

        if(!(commandSender.hasPermission("server-manager.permission")
                || commandSender.hasPermission("server-manager.*"))){
            Base.logMessage(player, LogState.WARNING, "You do not have the required permissions!");
            return true;
        }

        new DefaultInventory().openInventory(player);


        return false;
    }

}
