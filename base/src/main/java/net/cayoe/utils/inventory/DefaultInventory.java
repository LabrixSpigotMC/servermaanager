package net.cayoe.utils.inventory;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DefaultInventory {

    public void openInventory(final Player player) {
        new Menu("§7§l┃ §6§lSERVER", 9 * 6, 53, new ItemBuilder(Material.REDSTONE).setDisplayName("§aBack").build()
        ).open(player, menuContainer -> {

            for (int i = 0; i < 9; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            for (int i = 43; i < 53; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

            menuContainer.setItem(53, new ItemBuilder(Material.REDSTONE).setDisplayName("§aBack").build());

            menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullTexture("bd9f18c9d85f92f72f864d67c1367e9a45dc10f371549c46a4d4dd9e4f13ff4",
                            "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                    .addLore("§8› §7Main menu §8‹")
                    .build());

            Base.getModuleHandler().getModules().forEach(cachedModule -> {
                if (cachedModule.needPermission()) {
                    if (player.hasPermission(cachedModule.permission())) {
                        if (cachedModule.hasSkullID() && cachedModule.getSkullID() != null) {
                            menuContainer.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture(cachedModule.getSkullID(), cachedModule.name())
                                    .setLore("§7Description:", "§7› §8§o" + cachedModule.description()).build());
                        } else {
                            menuContainer.addItem(new ItemBuilder(cachedModule.material()).setDisplayName(cachedModule.name())
                                    .setLore("§7Description:", "§7› §8§o" + cachedModule.description()).build());
                        }
                    }
                    return;
                }

                if (cachedModule.hasSkullID() && cachedModule.getSkullID() != null) {
                    menuContainer.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture(cachedModule.getSkullID(), cachedModule.name())
                            .setLore("§7Description:", "§7› §8§o" + cachedModule.description()).build());
                } else {
                    menuContainer.addItem(new ItemBuilder(cachedModule.material()).setDisplayName(cachedModule.name())
                            .setLore("§7Description:", "§7› §8§o" + cachedModule.description()).build());
                }
            });
        });
    }
}
