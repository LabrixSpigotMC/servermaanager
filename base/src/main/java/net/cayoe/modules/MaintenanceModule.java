package net.cayoe.modules;

import com.google.common.collect.Lists;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.UUIDFetcher;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MaintenanceModule extends Module {

    private File file = new File("plugins/ServerManager/maintenance.yml");
    private static YamlConfiguration configuration;

    private List<UUID> changeMessagePlayers;
    private List<UUID> playersChangeList;

    @Override
    public void onLoad() {
        configuration = YamlConfiguration.loadConfiguration(file);

        changeMessagePlayers = Lists.newArrayList();
        playersChangeList = Lists.newArrayList();

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(configuration.get("maintenance") == null)
            configuration.set("maintenance", true);
        if(configuration.get("message") == null)
            configuration.set("message", "&cThe server is currently in maintenance mode");
        if(configuration.get("list") == null)
            configuration.set("list", Arrays.asList("cayoe"));
        saveConfig();

        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new PlayerChatListener());
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
        new Menu("§8§l» §aMaintenance", 9, 8, new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build())
                .open(player, menuContainer -> {
                    menuContainer.setItem(0, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("f2052977923a080c4a0e4bdab7905b811f3c85ebeb7bb97ad82e334c6fb91bee", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                            .addLore("§8› §7Maintenance menu §8‹").build());

                    menuContainer.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                            .setDisplayName(" ").build());

                    if(configuration.getBoolean("maintenance"))
                        menuContainer.setItem(2, new ItemBuilder(Material.GREEN_DYE)
                                        .addLore("§8§oWhen you activate the module, the", "§8§omaintenance mode is activated.")
                                .setDisplayName("§8» §aActivate module").build());
                    else
                        menuContainer.setItem(2, new ItemBuilder(Material.GRAY_DYE)
                                .addLore("§8§oWhen you disable the module, the", "§8§omaintenance mode is disabled.")
                                .setDisplayName("§8» §cDisable module").build());

                    menuContainer.setItem(3, new ItemBuilder(Material.PAPER)
                            .setDisplayName("§8» §6Message")
                            .addLore("§8› §r " + ChatColor.translateAlternateColorCodes('&', configuration.getString("message")) + "§r §8‹").build());

                    menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD).setOwner(player.getName())
                            .setDisplayName("§8» §ePlayer list").build());
        });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String name() {
        return "§8│ §aMaintenance";
    }

    @Override
    public String realName() {
        return "maintenance";
    }

    @Override
    public String description() {
        return "In maintenance mode, only specific players are allowed on the server.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.maintenance";
    }

    @Override
    public Boolean needPermission() {
        return false;
    }

    @Override
    public Boolean hasSkullID() {
        return true;
    }

    @Override
    public String getSkullID() {
        return "f2052977923a080c4a0e4bdab7905b811f3c85ebeb7bb97ad82e334c6fb91bee";
    }

    public class PlayerListInventory {

        private final Player player;

        public PlayerListInventory(Player player) {
            this.player = player;
            open();
        }

        private void open(){
            new ScrollableMenu("§cMaintenance players", 9*6, 9, 9*4, 53, 9*5 + 1, 9*5,
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76", "§7§l✖ §8┃ §cNo more pages found.").addLore("§8§oThere are no other pages.").build(),
                    new ItemBuilder(Material.REDSTONE).build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be", "§7§l» §8┃ §eNext page").build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d", "§7§l« §8┃ §ePage back").build())

                    .open(player, menuContainer -> {
                        for (int i = 0; i < 9; i++)
                            menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                        for (int i = 9*4; i < 9*6; i++)
                            menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                        menuContainer.setItem(48, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716", "§8┃ §aAdd player").build());

                        for(String names : configuration.getStringList("list")) {
                            final ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                                    .setDisplayName("§b§e" + names)
                                    .addLore("§8§oDouble click to remove the player from the list");
                            menuContainer.addSortItem(new ItemBuilder.Spigot(itemBuilder).setSkullTexture(UUIDFetcher.getUUID(names)).build());
                        }
                    });
        }
    }

    public class PlayerChatListener implements Listener {

        @EventHandler
        public void handle(final PlayerChatEvent event){
            final Player player = event.getPlayer();

            if(changeMessagePlayers.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    changeMessagePlayers.remove(player.getUniqueId());
                    openInventory(player);
                    return;
                }

                configuration.set("message", event.getMessage());
                saveConfig();

                player.sendMessage(Base.PREFIX + "§a§oYou have successfully changed the config.");

                openInventory(player);
                changeMessagePlayers.remove(player.getUniqueId());

                return;
            }

            if(playersChangeList.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playersChangeList.remove(player.getUniqueId());
                    new PlayerListInventory(player).open();
                    return;
                }

                final List<String> list = configuration.getStringList("list");

                if(UUIDFetcher.getUUID(event.getMessage()) == null){
                    player.sendMessage("§cError: name");
                    player.sendMessage("§e§oTry again");
                    return;
                }

                list.add(event.getMessage());

                configuration.set("list", list);
                saveConfig();

                player.sendMessage(Base.PREFIX + "§a§oYou have successfully changed the config.");

                new PlayerListInventory(player).open();
                playersChangeList.remove(player.getUniqueId());
                return;
            }
        }
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(event.getView().getTitle().equals("§8§l» §aMaintenance")){
                event.setCancelled(true);

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §aActivate module")){
                    configuration.set("maintenance", false);
                    saveConfig();
                    openInventory(player);
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cDisable module")){
                    configuration.set("maintenance", true);
                    saveConfig();
                    openInventory(player);
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §6Message")){
                    changeMessagePlayers.add(player.getUniqueId());
                    player.closeInventory();

                    player.sendMessage(Base.PREFIX + "§7Write the new message in the chat.");
                    player.sendMessage(Base.PREFIX + "§7You can cancel the process at any time with §c\"cancel\"§7.");
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §ePlayer list")){
                    new PlayerListInventory(player);
                    return;
                }

                return;
            }

            if(event.getView().getTitle().equals("§cMaintenance players")){
                event.setCancelled(true);

                System.out.println(event.getClick());

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8┃ §aAdd player")){
                    playersChangeList.add(player.getUniqueId());
                    player.closeInventory();

                    player.sendMessage(Base.PREFIX + "§8§oWrite the name of the player in the chat to add him to the list.");
                    player.sendMessage(Base.PREFIX + "§8§oYou can cancel the process by writing §c\"cancel\"§8§p in the chat.");

                    return;
                }

                if(event.getClick().equals(ClickType.DOUBLE_CLICK)){
                    if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§b§e")){
                        final List<String> list = configuration.getStringList("list");
                        final String name = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§b§e", "");

                        if(list.contains(name)){
                            list.remove(name);
                            configuration.set("list", list);
                            saveConfig();

                            new PlayerListInventory(player).open();
                        }
                    }
                }

                return;
            }

        }

    }

    public class PlayerJoinListener implements Listener {

        @EventHandler
        public void handle(final PlayerJoinEvent event){

        }

    }
}
