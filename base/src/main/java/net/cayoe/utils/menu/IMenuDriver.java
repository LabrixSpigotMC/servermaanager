package net.cayoe.utils.menu;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface IMenuDriver{

    void createMenu(final String title, final int size, final int backItemSlot, final ItemStack backItem, final Consumer<Menu> Consumer);
    void createMenu(final String title, final int size, final Consumer<Menu> Consumer);
    void createScrollableMenu(final String title, int size, int firstSlot, int lastSlot, final int backItemSlot, final int nextItemSlot, final int previousItemSlot, final ItemStack placeholderItem, final ItemStack backItem, final ItemStack previousItem, final Consumer<ScrollableMenu> Consumer);
    void createScrollableMenu(String title, int size, int firstSlot, int lastSlot, int nextItemSlot, int previousItemSlot, ItemStack placeholderItem, ItemStack nextItem, ItemStack previousItem, final Consumer<ScrollableMenu> Consumer);

    boolean isClickableItem(final ItemStack itemStack);
}
