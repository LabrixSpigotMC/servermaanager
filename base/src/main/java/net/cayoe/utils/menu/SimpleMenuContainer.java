package net.cayoe.utils.menu;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SimpleMenuContainer {

    private final HashMap<Integer, ItemStack> itemHashMap;

    protected SimpleMenuContainer() {
        this.itemHashMap = new LinkedHashMap<Integer, ItemStack>();
    }

    public void setItem(int index, ItemStack itemStack) {
        this.itemHashMap.put(index, itemStack);
    }

    public HashMap<Integer, ItemStack> getItemHashMap() {
        return this.itemHashMap;
    }
}
