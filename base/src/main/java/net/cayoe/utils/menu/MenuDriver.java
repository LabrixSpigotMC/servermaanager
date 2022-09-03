package net.cayoe.utils.menu;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuDriver implements IMenuDriver {


    public MenuDriver(){
    }

    @Override
    public boolean isClickableItem(ItemStack itemStack) {

        if(itemStack == null
                || !itemStack.hasItemMeta()
                || itemStack.getType() == null
                ||itemStack.getItemMeta().getDisplayName() == null)
            return false;

        return true;
    }

    @Override
    public void createMenu(final String title, final int size, final int backItemSlot, final ItemStack backItem, final Consumer<Menu> callback) {
        callback.accept(new Menu(title, size, backItemSlot, backItem));
    }

    @Override
    public void createMenu(final String title, final int size, final Consumer<Menu> callback) {
        callback.accept(new Menu(title, size));
    }

    @Override
    public void createScrollableMenu(final String title, int size, int firstSlot, int lastSlot, final int backItemSlot, final int nextItemSlot, final int previousItemSlot, final ItemStack placeholderItem, final ItemStack backItem, final ItemStack previousItem, final Consumer<ScrollableMenu> callback) {
        callback.accept(new ScrollableMenu(title, size, firstSlot, lastSlot, backItemSlot, nextItemSlot, previousItemSlot, placeholderItem, backItem, previousItem));
    }

    @Override
    public void createScrollableMenu(String title, int size, int firstSlot, int lastSlot, int nextItemSlot, int previousItemSlot, ItemStack placeholderItem, ItemStack nextItem, ItemStack previousItem, final Consumer<ScrollableMenu> callback) {
        callback.accept(new ScrollableMenu(title, size, firstSlot, lastSlot, nextItemSlot, previousItemSlot, placeholderItem, nextItem, previousItem));
    }

}
