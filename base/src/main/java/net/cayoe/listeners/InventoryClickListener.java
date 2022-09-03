package net.cayoe.listeners;

import net.cayoe.Base;
import net.cayoe.utils.inventory.DefaultInventory;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void handle(final InventoryClickEvent event){
        final Player player = (Player) event.getWhoClicked();
        final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getItemMeta() == null) return;

        switch (event.getView().getTitle()){
            case "§7§l┃ §6§lSERVER":
                event.setCancelled(true);

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l┃ §6§lSERVER MANAGER §8§l┃"))
                    new DefaultInventory().openInventory(player);

                if(!event.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE)){
                    for (Module cachedModule : Base.getModuleHandler().getCachedModules()) {
                        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(cachedModule.name())){
                            cachedModule.openInventory(player);
                        }
                    }
                }

                break;
        }

    }

}
