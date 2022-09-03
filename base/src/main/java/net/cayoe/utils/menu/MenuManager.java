package net.cayoe.utils.menu;

import net.cayoe.BukkitBootstrap;
import net.cayoe.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MenuManager{

    protected static final HashMap<Player, Menu> MENU_HASH_MAP = new LinkedHashMap<Player, Menu>();

    public MenuManager() {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onInventoryClick(InventoryClickEvent event) {
                if(event.getWhoClicked() instanceof Player) {
                    Player player = ((Player) event.getWhoClicked());

                    if(event.getView() != null) {
                        if(event.getCurrentItem() != null) {
                            ItemBuilder item = new ItemBuilder(event.getCurrentItem());

                            if(!(item.getType().equals(Material.AIR))) {

                                Menu menu = MenuManager.this.getPlayerMenu(player);
                                if(menu != null) {
                                    if(event.getView().getTitle().equals(menu.getTitle())) {

                                        if(menu.getBackItem() != null && item.getCurrencyItemStack().equals(menu.getBackItem())) {
                                            if(menu.getBackMenu() != null) {
                                                event.setCancelled(true);

                                                menu.close(player);
                                                menu.getBackMenu().reopen(player);
                                            }
                                        } else {
                                            if(menu instanceof ScrollableMenu) {
                                                ScrollableMenu scrollableMenu = ((ScrollableMenu) menu);

                                                if(scrollableMenu.getPreviousItem() != null && item.getCurrencyItemStack().equals(scrollableMenu.getPreviousItem())) {

                                                    event.setCancelled(true);

                                                    scrollableMenu.previousPage();
                                                } else if(scrollableMenu.getNextItem() != null && item.getCurrencyItemStack().equals(scrollableMenu.getNextItem())) {
                                                    event.setCancelled(true);

                                                    scrollableMenu.nextPage();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onInventoryClose(InventoryCloseEvent event) {
                if(event.getPlayer() instanceof Player) {
                    Player player = ((Player) event.getPlayer());

                    Menu menu = MenuManager.this.getPlayerMenu(player);
                    if(menu != null) {
                        if(event.getView().getTitle().equals(menu.getTitle())) {
                            menu.close(player);
                        }
                    }
                }
            }
        }, BukkitBootstrap.getInstance());
    }

    public Menu getPlayerMenu(Player player) {
        return MenuManager.MENU_HASH_MAP.get(player);
    }
}
