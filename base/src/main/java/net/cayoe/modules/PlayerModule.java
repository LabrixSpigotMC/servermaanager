package net.cayoe.modules;

import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PlayerModule extends Module {

    @Override
    public void onLoad() {
        Base.registerEvents(new InventoryClickListener());
    }

    @Override
    public void openInventory(Player player) {
        new ScrollableMenu("§7§l┃ §6Players", 9*6, 9, 9*4, 9*5+8, 9*5 + 1, 9*5,
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
            menuContainer.setItem(9*5 +3, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +4, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +5, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +6, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(9*5 +7, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

            menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                     .setSkullTexture("45f897ff78770aeadb9cb080ec85c9b15ef768eac334a9e709cd16c611d892d1", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                    .addLore("§8› §7Players §8‹")
                    .build());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                menuContainer.addSortItem(new ItemBuilder(Material.PLAYER_HEAD)
                        .setOwner(onlinePlayer.getName())
                        .setDisplayName("§6" + onlinePlayer.getName())
                        .addLore(" ", "§8› §7§oClick for more info").build());


        });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String name() {
        return "§8│ §5Players";
    }

    @Override
    public String realName() {
        return "players_module";
    }

    @Override
    public String description() {
        return "Control and manage players";
    }

    @Override
    public String permission() {
        return "server-manager.modules.players";
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
        return "45f897ff78770aeadb9cb080ec85c9b15ef768eac334a9e709cd16c611d892d1";
    }

    private void openSpecifInventory(final Player player, final Player target){
        new Menu("§d§f§a" + target.getName(), 9, 8, new ItemStack(Material.REDSTONE))
                .open(player, menuContainer -> {
                    menuContainer.setItem(0, new ItemBuilder(Material.PLAYER_HEAD).setOwner(target.getName()).setDisplayName("§6Player: §o" + target.getName())
                            .addLore("§8",
                                    "§8› §7§oHealth scale: §e" + target.getHealthScale(),
                                    "§8› §7§oGame mode: §e" + target.getGameMode(),
                                    "§8› §7§oCurrent world: §e" + target.getWorld().getName(),
                                    "§8› §7§oCurrent ping: §e" + target.getPing() + "ms")
                            .build());
                    menuContainer.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build());
                    menuContainer.setItem(2, new ItemBuilder(Material.SEA_PICKLE).setDisplayName("§8» §e§oChange health scale").addLore(" ", "§8› §7" + target.getHealthScale()).build());
                    menuContainer.setItem(3, new ItemBuilder(Material.SPECTRAL_ARROW).setDisplayName("§8» §e§oChange game mode").addLore(" ", "§8› §7" + target.getGameMode()).build());
                    menuContainer.setItem(4, new ItemBuilder(Material.ENDER_PEARL).setDisplayName("§8» §e§oTeleport to player").addLore(" ", "§8› §7" + target.getDisplayName()).build());
                    menuContainer.setItem(5, new ItemBuilder(Material.FEATHER).setDisplayName("§8» §e§oCan fly").addLore(" ", "§8› §7" + target.getAllowFlight()).build());
                    menuContainer.setItem(6, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§8» §e§oChange skin").addLore(" ", "§8› §cAttention: under development §7§l(§b§l§b☁§7§l)").build());
                });
    }

    private void openChoseGameModeInventory(final Player player, final Player target){
        new Menu("§b§d§f§a" + target.getName(), 9, 8, new ItemStack(Material.REDSTONE))
                .open(player, menuContainer -> {
                    menuContainer.setItem(0, new ItemBuilder(Material.PLAYER_HEAD).setOwner(target.getName()).setDisplayName("§6Gamemode: §o" + target.getName())
                            .addLore("§8§m------------", "§8› §7§oGame mode: §e" + target.getGameMode()).build());
                    menuContainer.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build());
                    menuContainer.setItem(2, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("441215d106cd5adedae717364f643c619a0c8826f2c6ac4e97ee8c944540b90e", "§8› §bCreative").build());
                    menuContainer.setItem(3, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc", "§8› §6Survival").build());
                    menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("53d1877be95a9edb86df2256f23958324c2ec19ef94277ce2fb5c3301841dc", "§8› §2Adventure").build());
                    menuContainer.setItem(5, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("80e16bf1c3df16524c10e91874fe81a8680f7684781fcaf554cc0c4d985d893c", "§8› §eSpectator").build());
                });
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(event.getView().getTitle().equals("§7§l┃ §6Players")){
                event.setCancelled(true);

                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§6")){
                    final String targetName = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§6", "");

                    if(Bukkit.getPlayer(targetName) == null){
                        openInventory(player);
                        return;
                    }
                    openSpecifInventory(player, Objects.requireNonNull(Bukkit.getPlayer(targetName)));
                }
                return;
            }

            if(event.getView().getTitle().startsWith("§d§f§a")){
                System.out.println(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName());

                event.setCancelled(true);

                final String targetReplaceName = event.getInventory().getItem(0).getItemMeta().getDisplayName();
                final String targetName = targetReplaceName.replaceAll("§6Player: §6§o", "");

                player.sendMessage(targetName);

                if(Bukkit.getPlayer(targetName) == null){
                    player.closeInventory();
                    openInventory(player);
                    return;
                }

                final Player target = Objects.requireNonNull(Bukkit.getPlayer(targetName));

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8» §e§oChange health scale":
                        double currentHealth = target.getHealth();

                        if(event.isRightClick()){
                            currentHealth -= 0.5;
                            target.setMaxHealth(currentHealth);
                            target.setHealth(currentHealth);
                        }else if(event.isLeftClick()){
                            currentHealth += 0.5;
                            target.setMaxHealth(currentHealth);
                            target.setHealth(currentHealth);
                        }
                        break;
                    case "§8» §e§oChange game mode":
                        openChoseGameModeInventory(player, target);
                        break;
                    case "§8» §e§oTeleport to player":
                        player.teleport(target.getLocation());
                        player.closeInventory();
                        break;
                    case "§8» §e§oCan fly":
                        if(player.getAllowFlight())
                            player.setAllowFlight(false);
                        else
                            player.setAllowFlight(true);
                        break;
                    case "§8» §e§oChange skin":
                        player.sendMessage("§b§lSorry! §b§oThis function is currently under development");
                        break;
                }
                openSpecifInventory(player, target);
                return;
            }

            if(event.getView().getTitle().startsWith("§b§d§f§a")){

                if(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName().startsWith("§6Gamemode: §o"))
                    return;

                event.setCancelled(true);

                final String targetName = event.getInventory().getItem(0).getItemMeta().getDisplayName().replaceAll("§6Gamemode: §o", "");

                if(Bukkit.getPlayer(targetName) == null){
                    player.closeInventory();
                    openInventory(player);
                    return;
                }

                final Player target = Objects.requireNonNull(Bukkit.getPlayer(targetName));

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8› §bCreative":
                        target.setGameMode(GameMode.CREATIVE);
                        break;
                    case "§8› §6Survival":
                        target.setGameMode(GameMode.SURVIVAL);
                        break;
                    case "§8› §2Adventure":
                        target.setGameMode(GameMode.ADVENTURE);
                        break;
                    case "§8› §eSpectator":
                        target.setGameMode(GameMode.SPECTATOR);
                        break;
                }

                openSpecifInventory(player, target);
            }

        }
    }
}
