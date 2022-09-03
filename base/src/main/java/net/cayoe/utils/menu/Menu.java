package net.cayoe.utils.menu;

import net.cayoe.BukkitBootstrap;
import net.cayoe.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Menu {

    private final String title;
    private final int size;

    private final int backItemSlot;
    private final ItemStack backItem;

    private MenuContainer container;
    private Consumer<MenuContainer> consumer;
    private Menu backMenu;

    private boolean defaultItems;
    private ItemStack defaultItemStack;

    public Menu(String title, int size, int backItemSlot, ItemStack backItem) {
        this.title = title;
        this.size = size;

        this.backItemSlot = backItemSlot;
        this.backItem = backItem;

        this.container = null;
        this.consumer = null;
        this.backMenu = null;

        this.defaultItems = false;
    }

    public Menu(String title, int size) {
        this(title, size, -1, null);
    }

    public void open(Player player, Consumer<MenuContainer> consumer) {
        player.openInventory(Bukkit.createInventory(player, this.size, "§8» §7Loading§8..."));

    //    Bukkit.getScheduler().runTaskAsynchronously(LobbySystem.getInstance(), () -> {

            (this.consumer = consumer).accept(this.container = new MenuContainer(Bukkit.createInventory(player, this.size, this.title), title));

            Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);

            if(menu != null && (this.backItemSlot != -1 && this.backItem != null)) {
                if(this.backMenu == null) {
                    this.backMenu = menu;
                }

                if(defaultItems)
                    for(int i = container.getInventory().getSize() - 9; i < container.getInventory().getSize(); i++)
                        container.setItem(i, defaultItemStack);

                this.container.setItem(this.backItemSlot, new ItemBuilder(this.backItem).setDisplayName("§aBack").build());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(this.container.getInventory()));
      //  });
    }

    public void openAsync(Player player, Consumer<MenuContainer> consumer) {
        player.openInventory(Bukkit.createInventory(player, this.size, "§8» §7Loading§8..."));

        Bukkit.getScheduler().runTaskAsynchronously(BukkitBootstrap.getInstance(), () -> {

            (this.consumer = consumer).accept(this.container = new MenuContainer(Bukkit.createInventory(player, this.size, this.title), title));

            Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);

            if(menu != null && (this.backItemSlot != -1 && this.backItem != null)) {
                if(this.backMenu == null) {
                    this.backMenu = menu;
                }

                if(defaultItems)
                    for(int i = container.getInventory().getSize() - 9; i < container.getInventory().getSize(); i++)
                        container.setItem(i, defaultItemStack);

                this.container.setItem(this.backItemSlot, new ItemBuilder(this.backItem).setDisplayName("§aBack").build());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(this.container.getInventory()));
        });
    }

    public void reopen(Player player) {
        player.openInventory(Bukkit.createInventory(player, this.size, "§8» §7Loading§8..."));

        Bukkit.getScheduler().runTaskAsynchronously(BukkitBootstrap.getInstance(), () -> {
            if(this.container == null || this.consumer == null) {
                return;
            }
            this.container.resetContainer();
            this.consumer.accept(this.container);

            Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);
            if(menu != null && (this.backItemSlot != -1 && this.backItem != null)) {
                if(this.backMenu == null) {
                    this.backMenu = menu;
                }
                this.container.setItem(this.backItemSlot, new ItemBuilder(this.backItem).setDisplayName("§aBack").build());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(this.container.getInventory()));
        });
    }

    public void close(Player player) {
        if(this.container != null && this.container.getTask() != null) {
            this.container.setTask(null);
        }
    }

    public String getTitle() {
        return this.title;
    }

    public int getSize() {
        return this.size;
    }

    public ItemStack getBackItem() {
        return this.backItem;
    }

    public MenuContainer getContainer() {
        return this.container;
    }

    protected void setContainer(MenuContainer container) {
        this.container = container;
    }

    protected Consumer<MenuContainer> getConsumer() {
        return this.consumer;
    }

    protected void setConsumer(Consumer<MenuContainer> consumer) {
        this.consumer = consumer;
    }

    protected int getBackItemSlot() {
        return this.backItemSlot;
    }

    public Menu getBackMenu() {
        return this.backMenu;
    }

    protected void setBackMenu(Menu backMenu) {
        this.backMenu = backMenu;
    }
}
