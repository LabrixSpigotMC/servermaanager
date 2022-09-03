package net.cayoe.utils.menu;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MenuContainer {

    private final Inventory inventory;

    private String titleName;

    private final HashMap<Integer, List<ItemStack>> pageHashMap;

    private final List<ItemStack> sortedItems;

    private final List<SimpleMenuContainer> subMenus;

    private BukkitTask task;

    private int currentPage;

    public MenuContainer(Inventory inventory, final String titleName) {
        this.inventory = inventory;

        this.titleName = titleName;

        this.pageHashMap = new LinkedHashMap<Integer, List<ItemStack>>();

        this.sortedItems = new LinkedList<ItemStack>();

        this.subMenus = new LinkedList<SimpleMenuContainer>();

        this.task = null;

        this.currentPage = 1;
    }

    public void resetContainer() {
        this.inventory.clear();

        this.pageHashMap.clear();

        this.sortedItems.clear();

        this.subMenus.clear();

        if(this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.currentPage = 1;
    }

    public void addSortItem(ItemStack itemStack) {
        this.sortedItems.add(itemStack);
    }

    public void addSubMenu(Consumer<SimpleMenuContainer> consumer) {
        SimpleMenuContainer container = new SimpleMenuContainer();
        consumer.accept(container);

        this.subMenus.add(container);
    }

    public ItemStack getItem(int index) {
        return this.inventory.getItem(index);
    }

    public void setItem(int index, ItemStack itemStack) {
        this.inventory.setItem(index, itemStack);
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return this.inventory.addItem(itemStacks);
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return this.inventory.removeItem(itemStacks);
    }

    public boolean contains(Material material) throws IllegalArgumentException {
        return this.inventory.contains(material);
    }

    public boolean contains(ItemStack itemStack) {
        return this.inventory.contains(itemStack);
    }

    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        return this.inventory.contains(material, amount);
    }

    public boolean contains(ItemStack itemStack, int amount) {
        return this.inventory.contains(itemStack, amount);
    }

    public boolean containsAtLeast(ItemStack itemStack, int amount) {
        return this.inventory.containsAtLeast(itemStack, amount);
    }

    public void remove(Material material) throws IllegalArgumentException {
        this.inventory.remove(material);
    }

    public void remove(ItemStack itemStack) {
        this.inventory.remove(itemStack);
    }

    public void clear(int index) {
        this.inventory.clear(index);
    }

    public void clear() {
        this.inventory.clear();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public HashMap<Integer, List<ItemStack>> getPageHashMap() {
        return this.pageHashMap;
    }

    public List<ItemStack> getSortedItems() {
        return this.sortedItems;
    }

    public List<SimpleMenuContainer> getSubMenus() {
        return this.subMenus;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        if(this.task != null) {
            this.task.cancel();
        }
        this.task = task;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getTitleName() {
        return titleName;
    }
}
