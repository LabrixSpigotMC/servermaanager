package net.cayoe.utils.menu;

import net.cayoe.BukkitBootstrap;
import net.cayoe.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ScrollableMenu extends Menu {

    private final int firstSlot, lastSlot, nextItemSlot, previousItemSlot;
    private final ItemStack placeholderItem, nextItem, previousItem;

    public ScrollableMenu(String title, int size, int firstSlot, int lastSlot, int backItemSlot, int nextItemSlot, int previousItemSlot, ItemStack placeholderItem, ItemStack backItem, ItemStack nextItem, ItemStack previousItem) {
        super(title, size, backItemSlot, backItem);

        this.firstSlot = firstSlot;
        this.lastSlot = lastSlot;
        this.nextItemSlot = nextItemSlot;
        this.previousItemSlot = previousItemSlot;
        this.placeholderItem = placeholderItem;
        this.nextItem = nextItem;
        this.previousItem = previousItem;
    }

    public ScrollableMenu(String title, int size, int firstSlot, int lastSlot, int nextItemSlot, int previousItemSlot, ItemStack placeholderItem, ItemStack nextItem, ItemStack previousItem) {
        this(title, size, firstSlot, lastSlot, -1, nextItemSlot, previousItemSlot, placeholderItem, null, nextItem, previousItem);
    }

    public ScrollableMenu(String title, int size, int firstSlot, int lastSlot, int backItemSlot, int nextItemSlot, int previousItemSlot, ItemStack backItem, ItemStack nextItem, ItemStack previousItem) {
        this(title, size, firstSlot, lastSlot, backItemSlot, nextItemSlot, previousItemSlot, null, backItem, nextItem, previousItem);
    }

    public ScrollableMenu(String title, int size, int nextItemSlot, int previousItemSlot, ItemStack placeholderItem, ItemStack nextItem, ItemStack previousItem) {
        this(title, size, -1, -1, -1, nextItemSlot, previousItemSlot, placeholderItem, null, nextItem, previousItem);
    }

    public ScrollableMenu(String title, int size, int backItemSlot, int nextItemSlot, int previousItemSlot, ItemStack backItem, ItemStack nextItem, ItemStack previousItem) {
        this(title, size, -1, -1, backItemSlot, nextItemSlot, previousItemSlot, null, backItem, nextItem, previousItem);
    }

    public ScrollableMenu(String title, int size, int firstSlot, int lastSlot, int nextItemSlot, int previousItemSlot, ItemStack nextItem, ItemStack previousItem) {
        this(title, size, firstSlot, lastSlot, -1, nextItemSlot, previousItemSlot, null, null, nextItem, previousItem);
    }

    public ScrollableMenu(String title, int lastSlot, int nextItemSlot, int previousItemSlot, ItemStack nextItem, ItemStack previousItem) {
        this(title, -1, -1, lastSlot, -1, nextItemSlot, previousItemSlot, null, null, nextItem, previousItem);
    }
    

    public void open(Player player, Consumer<MenuContainer> consumer) {
        player.openInventory(Bukkit.createInventory(player, super.getSize(), "§8» §7Loading§8..."));

    //    Bukkit.getScheduler().runTaskAsynchronously(LobbySystem.getInstance(), () -> {
            super.setContainer(new MenuContainer(Bukkit.createInventory(player, super.getSize(), super.getTitle()), getTitle()));

            super.setConsumer(consumer);
            super.getConsumer().accept(super.getContainer());

            Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);
            if(menu != null && (super.getBackItemSlot() != -1 && super.getBackItem() != null)) {
                if(super.getBackMenu() == null) {
                    super.setBackMenu(menu);
                }
                super.getContainer().setItem(super.getBackItemSlot(), new ItemBuilder(super.getBackItem()).setDisplayName("§aBack").build());
            }

            this.loadItems();
            this.transferItems();
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(super.getContainer().getInventory()));
     //   });
    }

    public void openAsync(Player player, Consumer<MenuContainer> consumer) {
        player.openInventory(Bukkit.createInventory(player, super.getSize(), "§8» §7Loading§8..."));

            Bukkit.getScheduler().runTaskAsynchronously(BukkitBootstrap
                    .getInstance(), () -> {
        super.setContainer(new MenuContainer(Bukkit.createInventory(player, super.getSize(), super.getTitle()), getTitle()));

        super.setConsumer(consumer);
        super.getConsumer().accept(super.getContainer());

        Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);
        if(menu != null && (super.getBackItemSlot() != -1 && super.getBackItem() != null)) {
            if(super.getBackMenu() == null) {
                super.setBackMenu(menu);
            }
            super.getContainer().setItem(super.getBackItemSlot(), new ItemBuilder(super.getBackItem()).setDisplayName("§aBack").build());
        }

        this.loadItems();
        this.transferItems();

        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(super.getContainer().getInventory()));
           });
    }

    public void reopen(Player player) {
        player.openInventory(Bukkit.createInventory(player, super.getSize(), "§8» §7Loading§8..."));

        Bukkit.getScheduler().runTaskAsynchronously(BukkitBootstrap.getInstance(), () -> {
            if(super.getContainer() == null || super.getConsumer() == null) {
                return;
            }
            super.getContainer().resetContainer();
            super.getConsumer().accept(super.getContainer());

            Menu menu = MenuManager.MENU_HASH_MAP.put(player, this);
            if(menu != null && (super.getBackItemSlot() != -1 && super.getBackItem() != null)) {
                if(super.getBackMenu() == null) {
                    super.setBackMenu(menu);
                }
                super.getContainer().setItem(super.getBackItemSlot(), new ItemBuilder(super.getBackItem()).setDisplayName("§aBack").build());
            }

            this.loadItems();
            this.transferItems();
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitBootstrap.getInstance(), () -> player.openInventory(super.getContainer().getInventory()));
        });
    }

    public ItemStack getNextItem() {
        return this.nextItem;
    }

    public ItemStack getPreviousItem() {
        return this.previousItem;
    }

    public boolean hasNextPage() {
        return super.getContainer() != null && super.getContainer().getCurrentPage() < ((super.getContainer().getPageHashMap().isEmpty()) ? super.getContainer().getSubMenus().size() : super.getContainer().getPageHashMap().size());
    }

    public void nextPage() {
        if(this.hasNextPage()) {
            super.getContainer().setCurrentPage(super.getContainer().getCurrentPage() + 1);
            this.clearItems();
            this.transferItems();
        }
    }

    public boolean hasPreviousPage() {
        return super.getContainer() != null && super.getContainer().getCurrentPage() > 1;
    }

    public void previousPage() {
        if(this.hasPreviousPage()) {
            super.getContainer().setCurrentPage(super.getContainer().getCurrentPage() - 1);
            this.clearItems();
            this.transferItems();
        }
    }

    private void loadItems() {
        if(super.getContainer() != null) {
            int page = 1;
            int count = 0;
            for(ItemStack itemStack : super.getContainer().getSortedItems()) {
                super.getContainer().getPageHashMap().putIfAbsent(page, new LinkedList<ItemStack>());
                super.getContainer().getPageHashMap().get(page).add(itemStack);

                if(++count >= (this.lastSlot - this.firstSlot)) {
                    page++;
                    count = 0;
                }
            }
            if((super.getContainer().getCurrentPage() > super.getContainer().getPageHashMap().size()) || (super.getContainer().getCurrentPage() > super.getContainer().getSubMenus().size())) {
                super.getContainer().setCurrentPage(1);
            }
        }
    }

    private void transferItems() {
        if(super.getContainer() != null) {
            if(!(super.getContainer().getPageHashMap().isEmpty()) || !(super.getContainer().getSubMenus().isEmpty())) {
                if(super.getContainer().getPageHashMap().isEmpty()) {
                    super.getContainer().getSubMenus().get(super.getContainer().getCurrentPage()).getItemHashMap().forEach(((index, itemStack) -> super.getContainer().setItem(index, itemStack)));
                } else {
                    AtomicInteger index = new AtomicInteger(this.firstSlot);
                    super.getContainer().getPageHashMap().get(super.getContainer().getCurrentPage()).forEach(itemStack -> super.getContainer().setItem(index.getAndIncrement(), itemStack));
                }
            }

            if(this.previousItemSlot != -1 && this.previousItem != null) {
                if(this.hasPreviousPage() || this.placeholderItem == null) {
                    super.getContainer().setItem(this.previousItemSlot, new ItemBuilder(this.previousItem).setDisplayName("§aPrevious page").setLore("§7Site " + (super.getContainer().getCurrentPage() -1)).build());
                } else {
                    super.getContainer().setItem(this.previousItemSlot, this.placeholderItem);
                }
            }

            if(this.nextItemSlot != -1 && this.nextItem != null) {
                if(this.hasNextPage() || this.placeholderItem == null) {
                    super.getContainer().setItem(this.nextItemSlot, new ItemBuilder(this.nextItem).setDisplayName(this.nextItem.getItemMeta().getDisplayName()).setLore("§7Site " + super.getContainer().getCurrentPage()).build());
                } else {
                    super.getContainer().setItem(this.nextItemSlot, this.placeholderItem);
                }
            }
        }
    }

    private void clearItems() {
        if(super.getContainer() != null) {
            ((super.getContainer().getSortedItems().isEmpty()) ? super.getContainer().getSubMenus().get(super.getContainer().getCurrentPage()).getItemHashMap().values() : super.getContainer().getSortedItems()).forEach(itemStack -> super.getContainer().remove(itemStack));
        }
    }
}
