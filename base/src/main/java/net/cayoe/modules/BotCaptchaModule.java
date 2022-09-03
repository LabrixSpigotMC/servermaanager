package net.cayoe.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotCaptchaModule extends Module {

    public final String PREFIX = "§8| §6BotCaptcha §8§l» §7";

    private File file = new File("plugins/ServerManager/bot_captcha.yml");
    private static YamlConfiguration configuration;

    private final Map<UUID, Integer> playerChatVerify = Maps.newHashMap();

    private List<UUID> playersWhoAreInLoginState;

    @Override
    public void onLoad() {
        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new PlayerJoinListener());
        Base.registerEvents(new PlayerChatListener());
        Base.registerEvents(new PreventListener());

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        playersWhoAreInLoginState = Lists.newArrayList();
        configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.get("botcaptcha_use") == null)
            configuration.set("botcaptcha_use", false);
        if(configuration.get("verify_mode") == null)
            configuration.set("verify_mode", Captcha.CHAT_CAPTCHA.getText());
        if(configuration.get("data_mode") == null)
            configuration.set("data_mode", "FILE");
        if(configuration.get("only_on_first_join") == null)
            configuration.set("only_on_first_join", true);
        if(configuration.get("player_data") == null)
            configuration.set("player_data", Arrays.asList("null"));

        saveConfig();
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
        new Menu("§8§l» §dBotCaptcha", 9*6, 53, new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build())
                .open(player, menuContainer -> {
                    for (int i = 0; i < 9; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    for (int i = 9*4; i < 9*6; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                    menuContainer.setItem(11, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(20, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(29, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                    menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("cde33c95fec1b8d988250f5f5b3a2485742439faeaa75ed506ea01d75e17f21", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                            .addLore("§8› §7BotCaptcha menu §8‹")
                            .build());

                    if(!configuration.getBoolean("botcaptcha_use"))
                        menuContainer.setItem(19, new ItemBuilder(Material.LIME_DYE).setDisplayName("§8§l» §aEnable Module")
                                .addLore("§8§oWhen activating the module,", "§8§oeach player must pass the captcha at least once when connecting.")
                                .build());
                    else
                        menuContainer.setItem(19, new ItemBuilder(Material.GRAY_DYE).setDisplayName("§8§l» §cDisable Module")
                                .addLore("§8§oClick to deactivate the module with its functions")
                                .build());

                    menuContainer.setItem(18, new ItemBuilder(Material.PAPER).setDisplayName("§8§l» §aCurrent settings")
                            .addLore("  ", "§8| §aActive §8» §7" + configuration.getBoolean("botcaptcha_use"), "§8| §6Verify mode §8» §7" + configuration.getString("verify_mode"))
                            .build());

                    menuContainer.setItem(22, new ItemBuilder(Material.PAPER).setDisplayName("§8§l» §3Map-Captcha")
                            .addLore("§8§oA text is displayed on a card,", "§8§othis text must be written by", "§8§othe player in the chat.", "§c§oWARNING: This function is currently still under development.").build());
                    menuContainer.setItem(23, new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("§8§l» §3Chat-Captcha")
                            .addLore("§8§oThe player has to repeat a certain", "§8§onumber from the chat.").build());
                    menuContainer.setItem(24, new ItemBuilder(Material.BAMBOO).setDisplayName("§8§l» §3ChatClick-Captcha")
                            .addLore("§8§oThe player must click on a message", "§8§o in the chat.", "§c§oWARNING: This function is currently still under development.").build());
                    menuContainer.setItem(25, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("51e51dcdc687d3ab7d599fb74ff362f900dafd294da11d2246f571944ad73711", "§8§l» §3GUI-Captcha")
                            .addLore("§8§oThe player must click on a specific item in a GUI.", "§c§oWARNING: This function is currently still under development.").build());

                    menuContainer.setItem(45, new ItemBuilder(Material.COMPARATOR).setDisplayName("§8§l» §5Safe data mode")
                            .addLore("  ", "§8| §6Current §8§l» §b§l" + configuration.getString("data_mode"), "  ").build());
                    menuContainer.setItem(46, new ItemBuilder(Material.SOUL_TORCH).setDisplayName("§8§l» §eOnly on first join")
                            .addLore("  ", "§8| §6Current §8§l» §a§l" + configuration.getBoolean("only_on_first_join"), "  ").build());

                });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String name() {
        return "§8│ §dBotCaptcha";
    }

    @Override
    public String realName() {
        return "bot_captcha";
    }

    @Override
    public String description() {
        return "Prevent bots on your server through different types of verification.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.bot_captcha";
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
        return "cde33c95fec1b8d988250f5f5b3a2485742439faeaa75ed506ea01d75e17f21";
    }

    public void verifyPlayer(final UUID uuid){
        final List<String> uuidList = configuration.getStringList("player_data");
        uuidList.add(uuid.toString());

        configuration.set("player_data", uuidList);
        saveConfig();
    }

    public enum Captcha {

        CHAT_CLICK_CAPTCHA("CHAT_CLICK"),
        MAP_CAPTCHA("MAP_CAPTCHA"),
        CHAT_CAPTCHA("CHAT_CAPTCHA"),
        GUI_CAPTCHA("GUI_CAPTCHA");

        private final String text;

        Captcha(String text){
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getView().getTitle().equals("§8§l» §dBotCaptcha")){
                event.setCancelled(true);

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8§l» §aEnable Module":
                        configuration.set("botcaptcha_use", true);
                        saveConfig();

                        openInventory(player);
                        break;
                    case "§8§l» §cDisable Module":
                        configuration.set("botcaptcha_use", false);
                        saveConfig();

                        openInventory(player);
                        break;
                    case "§8§l» §3Map-Captcha":
                    case "§8§l» §3GUI-Captcha":
                    case "§8§l» §3ChatClick-Captcha":
                        /*
                        configuration.set("verify_mode", Captcha.CHAT_CLICK_CAPTCHA.getText());
                        saveConfig();

                        openInventory(player);
                         **/

                        /*
                        configuration.set("verify_mode", Captcha.GUI_CAPTCHA.getText());
                        saveConfig();

                        openInventory(player);
                        **/

                        player.sendMessage(Base.PREFIX + "§bThis function is currently still under development.");

                        /*
                        configuration.set("verify_mode", Captcha.MAP_CAPTCHA.getText());
                        saveConfig();

                        openInventory(player);
                         **/
                        break;
                    case "§8§l» §3Chat-Captcha":
                        configuration.set("verify_mode", Captcha.CHAT_CAPTCHA.getText());
                        saveConfig();

                        openInventory(player);
                        break;

                    case "§8§l» §5Safe data mode":
                        player.sendMessage(Base.PREFIX + "§cYou cannot change that.");
                        break;
                    case "§8§l» §eOnly on first join":
                        if(configuration.getBoolean("only_on_first_join"))
                            configuration.set("only_on_first_join", false);
                        else
                            configuration.set("only_on_first_join", true);
                        saveConfig();

                        openInventory(player);
                        break;
                }

            }

        }

    }

    public class PlayerJoinListener implements Listener {

        @EventHandler
        public void handle(final PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            if (configuration.getBoolean("botcaptcha_use")) {
                final List<String> uuidList = configuration.getStringList("player_data");

                player.setGameMode(GameMode.SPECTATOR);

                if (configuration.getBoolean("only_on_first_join")) {
                    for (String s : uuidList)
                        if (!s.equals("null"))
                            if (player.getUniqueId().equals(UUID.fromString(s)))
                                return;

                    player.setGameMode(GameMode.SPECTATOR);
                    playersWhoAreInLoginState.add(player.getUniqueId());

                    if (Objects.requireNonNull(configuration.getString("verify_mode")).equals(Captcha.CHAT_CAPTCHA.getText())) {
                        int random_int = (int) Math.floor(Math.random() * (2000 - 100 + 1) + 300);

                        playerChatVerify.put(player.getUniqueId(), random_int);

                        player.sendMessage(PREFIX + "§ePlease write the following numbers in the chat to verify yourself.");
                        player.sendMessage(PREFIX + "§7ID: §e" + random_int);
                    }
                    return;
                }

                player.setGameMode(GameMode.SPECTATOR);
                playersWhoAreInLoginState.add(player.getUniqueId());

                if (Objects.requireNonNull(configuration.getString("verify_mode")).equals(Captcha.CHAT_CAPTCHA.getText())) {
                    int random_int = (int) Math.floor(Math.random() * (2000 - 100 + 1) + 300);

                    playerChatVerify.put(player.getUniqueId(), random_int);

                    player.sendMessage(PREFIX + "§ePlease write the following numbers in the chat to verify yourself.");
                    player.sendMessage(PREFIX + "§7ID: §e" + random_int);
                }
            }
        }
    }

    public class PlayerChatListener implements Listener {

        @EventHandler
        public void handle(final PlayerChatEvent event) {
            final Player player = event.getPlayer();

            if (configuration.getBoolean("botcaptcha_use")) {


                if (playersWhoAreInLoginState.contains(player.getUniqueId())) {
                    event.setCancelled(true);

                    String selfNumberString = event.getMessage();

                    for (ChatColor chatColor : ChatColor.values()) {
                        selfNumberString = selfNumberString.replaceAll(chatColor.toString(), "");
                    }

                    final Integer number = playerChatVerify.get(player.getUniqueId());

                    final Integer selfNumber = Integer.parseInt(selfNumberString);


                    if (playerChatVerify.containsKey(player.getUniqueId())) {
                        try {

                            if (number.toString().equalsIgnoreCase(selfNumber.toString())) {

                                verifyPlayer(player.getUniqueId());
                                playersWhoAreInLoginState.remove(player.getUniqueId());
                                playerChatVerify.remove(player.getUniqueId());

                                player.sendMessage(PREFIX + "§aYou have successfully verified");
                                player.setGameMode(GameMode.SURVIVAL);

                                return;
                            }

                            playersWhoAreInLoginState.remove(player.getUniqueId());
                            playerChatVerify.remove(player.getUniqueId());

                            player.kickPlayer(PREFIX + "§cYou wrote the wrong number.");
                        } catch (NumberFormatException exception) {
                            playersWhoAreInLoginState.remove(player.getUniqueId());
                            playerChatVerify.remove(player.getUniqueId());

                            player.kickPlayer(PREFIX + "§cYou wrote the wrong number.");
                        }
                    }

                }
            }
        }

    }

    public class PreventListener implements Listener {

        @EventHandler
        public void handlePlayerMove(final PlayerMoveEvent event){
            if(playersWhoAreInLoginState.contains(event.getPlayer().getUniqueId()))
                event.setCancelled(true);

        }

        @EventHandler
        public void handleInventoryClick(final InventoryClickEvent event){
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(playersWhoAreInLoginState.contains(event.getWhoClicked().getUniqueId()))
                event.getWhoClicked().closeInventory();
        }

        @EventHandler
        public void handlePlayerInteract(final PlayerInteractEvent event){
            if(playersWhoAreInLoginState.contains(event.getPlayer().getUniqueId()))
                event.setCancelled(true);
        }

    }
}
