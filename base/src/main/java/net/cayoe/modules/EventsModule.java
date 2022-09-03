package net.cayoe.modules;

import com.google.common.collect.Lists;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EventsModule extends Module {

    private List<ModularEvent> modularEvents;
    private File file;
    private static YamlConfiguration configuration;

    @Override
    public void onLoad() {
        this.modularEvents = Lists.newArrayList();

        file = new File("plugins/ServerManager/events.yml");
        configuration = YamlConfiguration.loadConfiguration(file);

        Base.registerEvents(new InventoryClickListener());

        createDefaultEvents();
        confirmConfiguration();
    }

    private void saveConfig(){
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openInventory(Player player) {
        new ScrollableMenu("§7§l┃ §a§lEvents", 9*6, 9, 9*4, 9*5+8, 9*5 + 1, 9*5,
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76", "§7§l✖ §8┃ §cNo more pages found.").addLore("§8§oThere are no other pages.").build(),
                new ItemBuilder(Material.REDSTONE).build(),
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be", "§7§l» §8┃ §eNext page").build(),
                new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d", "§7§l« §8┃ §ePage back").build()
        ).open(player, menuContainer -> {
            for (int i = 0; i < 9; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            for (int i = 9*4; i < 9*5; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +2, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +7, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

            menuContainer.setItem(4, new ItemBuilder(Material.PAINTING)
                    .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                    .addLore("§8› §7Events §8‹")
                    .build());

            menuContainer.setItem(9*5 + 3, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());
            menuContainer.setItem(9*5 + 4, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName(" ").build());
            menuContainer.setItem(9*5 + 5, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§8┃ §c§oReset all").build());
            menuContainer.setItem(9*5 + 6, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());

            for(ModularEvent modularEvent : modularEvents)
                menuContainer.addSortItem(new ItemBuilder(Material.SNOWBALL).setDisplayName("§6" + modularEvent.getName())
                        .addLore("§7› §oDescription: " + modularEvent.getDescription()).build());
        });
    }

    @Override
    public Material material() {
        return Material.PAINTING;
    }

    @Override
    public String name() {
        return "§8│ §a§lEvents";
    }

    @Override
    public String realName() {
        return "events_module";
    }

    @Override
    public String description() {
        return "Configure events and what should happen";
    }

    @Override
    public String permission() {
        return "server-manager.modules.events";
    }

    @Override
    public Boolean needPermission() {
        return false;
    }

    private void createDefaultEvents(){
        modularEvents.add(new ModularEvent("PlayerJoinEvent", "Performed when a player join",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Change join message").build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Disable message").build()
                ), new PlayerJoin(), Arrays.asList("PlayerJoinEvent.joinMessage", "PlayerJoinEvent.useMessage"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        modularEvents.add(new ModularEvent("PlayerQuitEvent", "Performed when a player quit",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Change quit message").addLore().build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Disable message").addLore().build()
                ), new PlayerQuit(), Arrays.asList("PlayerQuitEvent.quitMessage", "PlayerQuitEvent.useMessage"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        modularEvents.add(new ModularEvent("PlayerChatEvent", "Performed when a player chat something",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set cancelled").addLore().build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set format").addLore().build()
                ), new PlayerChat(), Arrays.asList("PlayerChatEvent.cancelled", "PlayerChatEvent.format"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        modularEvents.add(new ModularEvent("PlayerFishEvent", "Performed when a player fishing",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Change xp amount").addLore().build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set cancelled").addLore().build()
                ), new PlayerFish(), Arrays.asList("PlayerFishEvent.cancelled", "PlayerFishEvent.xp"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        modularEvents.add(new ModularEvent("PlayerBedEnter", "Performed when a player enter to bed",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set use bed").addLore().build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set cancelled").addLore().build()
                ), new PlayerBedEnter(), Arrays.asList("PlayerBedEnter.useBed", "PlayerBedEnter.cancelled"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        modularEvents.add(new ModularEvent("PlayerBedLeave", "Performed when a player leave the bed",
                Arrays.asList(
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set Spawn Location").addLore().build(),
                        new ItemBuilder(Material.WARPED_DOOR).setDisplayName("§8› §3Set cancelled").addLore().build()
                ), new PlayerBedLeave(), Arrays.asList("PlayerBedLeave.setSpawnLocation", "PlayerBedLeave.cancelled"), Arrays.asList("%DEFAULT%", "%DEFAULT%")));

        final List<String> namedEvents = Lists.newArrayList();

        for (ModularEvent modularEvent : modularEvents)
            namedEvents.add(modularEvent.getName());

        configuration.set("ModuleEvents", namedEvents);
        saveConfig();
    }

    private void confirmConfiguration(){

        for(int count = 0; count < modularEvents.size(); count++){
            final ModularEvent modularEvent = modularEvents.get(count);

            for (int pathCount = 0; count < modularEvent.getPaths().size(); count++)
                configuration.set(modularEvent.getPaths().get(0), modularEvent.getValue().get(0));
        }

        saveConfig();
    }

    public ModularEvent getModularEvent(final String name){
        for (ModularEvent event : this.modularEvents) {
            if(event.getName().equalsIgnoreCase(name))
                return event;
        }
        return null;
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(event.getView().getTitle().equals("§7§l┃ §a§lEvents")){
                event.setCancelled(true);

                if(event.getCurrentItem().getType().equals(Material.SNOWBALL) && event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§6")){
                    final String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§6", "");

                    if(getModularEvent(name) == null)
                        return;

                    final ModularEvent modularEvent = getModularEvent(name);
                    openSpecifEventInventory(player, modularEvent);
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§6") && event.getView().getItem(0).getItemMeta().getDisplayName().startsWith("§eEvent:")){
                    final String moduleEventName = event.getView().getItem(0).getItemMeta().getDisplayName().replaceAll("§eEvent: ", "");

                    if(getModularEvent(moduleEventName) == null) return;

                    final ModularEvent modularEvent = getModularEvent(moduleEventName);

                    if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§8› §3") && event.getCurrentItem().getType().equals(Material.WARPED_DOOR)){
                        if(modularEvent.getName().equals("PlayerJoinEvent")){
                        }
                    }

                    return;
                }
            }
        }

    }

    private void openSpecifEventInventory(final Player player , final ModularEvent modularEvent){
         new Menu("§1§6" + modularEvent.getName(), 9, 8, new ItemStack(Material.REDSTONE))
                .open(player, menuContainer -> {
                    menuContainer.setItem(0, new ItemBuilder(Material.SNOWBALL).setDisplayName("§eEvent: " + modularEvent.getName())
                            .addLore("§7§oDescription: §7" + modularEvent.getDescription()).build());
                    menuContainer.setItem(1, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());

                    for (ItemStack modularEventItemStack : modularEvent.getItemStacks())
                        menuContainer.addItem(modularEventItemStack);
                });
    }

    public class ModularEvent {

        private final String name;
        private final String description;

        private final List<ItemStack> itemStacks;
        public final Listener bukkitEvent;

        private final List<String> paths;
        private final List<String> value;

        private boolean hasPlayer;

        public ModularEvent(String name, String description, List<ItemStack> itemStacks, Listener bukkitEvent, List<String> paths, List<String> value) {
            this.name = name;
            this.description = description;
            this.itemStacks = itemStacks;
            this.bukkitEvent = bukkitEvent;
            this.paths = paths;
            this.value = value;

            this.hasPlayer = true;
        }

        public void setHasPlayer(boolean hasPlayer) {
            this.hasPlayer = hasPlayer;
        }

        public boolean isHasPlayer() {
            return hasPlayer;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

        public Listener getBukkitEvent() {
            return bukkitEvent;
        }

        public List<ItemStack> getItemStacks() {
            return itemStacks;
        }

        public List<String> getPaths() {
            return paths;
        }

        public List<String> getValue() {
            return value;
        }
    }

    public class PlayerJoin implements Listener {

        @EventHandler
        public void handle(final PlayerJoinEvent event){
        }
    }

    public class PlayerQuit implements Listener {

        @EventHandler
        public void handle(final PlayerQuitEvent event){
        }
    }

    public class PlayerChat implements Listener {

        @EventHandler
        public void handle(final AsyncPlayerChatEvent event){
        }
    }

    public class PlayerFish implements Listener {

        @EventHandler
        public void handle(final PlayerFishEvent event){
        }
    }

    public class PlayerBedEnter implements Listener {

        @EventHandler
        public void handle(final PlayerBedEnterEvent event){
        }
    }

    public class PlayerBedLeave implements Listener {

        @EventHandler
        public void handle(final PlayerBedLeaveEvent event){
        }
    }

}
