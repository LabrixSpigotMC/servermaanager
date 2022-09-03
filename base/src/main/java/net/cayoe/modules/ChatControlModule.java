package net.cayoe.modules;

import com.google.common.collect.Lists;
import net.cayoe.Base;
import net.cayoe.BukkitBootstrap;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.MenuContainer;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatControlModule extends Module {

    private File file = new File("plugins/ServerManager/chat.yml");
    private static YamlConfiguration configuration;

    private final List<UUID> playersWhoChangeClearChatMessage = Lists.newArrayList();
    private final List<UUID> playersWhoChangePunishChatMessage = Lists.newArrayList();

    private final List<UUID> playersWhoCreateBannedWord = Lists.newArrayList();

    private BukkitTask clearChatTaskID;

    @Override
    public void onLoad() {
        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new PlayerChatListener());

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.get("chat_active") == null)
            configuration.set("chat_active", true);
        if(configuration.get("chat_color") == null)
            configuration.set("chat_color", ChatColor.WHITE.asBungee().name());
        if(configuration.get("chat_clear_message") == null)
            configuration.set("chat_clear_message", "&8| &4Chat has been cleared.");
        if(configuration.get("autoclearchat.use") == null)
            configuration.set("autoclearchat.use", false);
        if(configuration.get("autoclearchat.timer") == null)
            configuration.set("autoclearchat.timer", 5);
        if(configuration.getList("chat_banned_words_list") == null)
            configuration.set("chat_banned_words_list", Arrays.asList("Example"));
        if(configuration.get("chat_banned_words_punishment_action") == null)
            configuration.set("chat_banned_words_punishment_action", "WARNING_MESSAGE");
        if(configuration.get("chat_punish_message") == null)
            configuration.set("chat_punish_message", "&cYou can't write that.");

        saveConfig();

        if(clearChatTaskID != null)
            stopTask(clearChatTaskID);
        if(configuration.getBoolean("autoclearchat.use"))
            startAutoClearChatTask(configuration.getInt("autoclearchat.timer"));
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
        new Menu("§8│ §f§lCHATCONTROL", 9*6, 53, new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build())
                .open(player, menuContainer -> {
                    for (int i = 0; i < 9; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    for (int i = 43; i < 53; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                    menuContainer.setItem(4, new ItemBuilder(Material.PAPER)
                            .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                            .addLore("§8› §7ChatControl menu §8‹")
                            .build());

                    if(configuration.getBoolean("chat_active"))
                        menuContainer.setItem(9*2 + 1, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37", "§8§l» §cDisable chat")
                                .addLore("§8§oThe chat is currently enabled.", "§7Click to disable the chat.").build());
                    else
                        menuContainer.setItem(9*2 + 1, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("930f4537d214d38666e6304e9c851cd6f7e41a0eb7c25049c9d22c8c5f6545df", "§8§l» §aEnable chat")
                                .addLore("§8§oThe chat is currently not enabled.", "§7Click to enable the chat.").build());

                    menuContainer.setItem(9*2 + 2, new ItemBuilder(Material.BLUE_DYE)
                            .setDisplayName("§8§l» §9Change default chat color")
                            .addLore("§8§oCurrent color: §r" + (ChatColor.valueOf((String) configuration.get("chat_color"))) + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_color")))).build());

                    menuContainer.setItem(9*2 + 3, new ItemBuilder(Material.BARRIER)
                            .setDisplayName("§8§l» §4Chat clear").addLore("§8§oThe chat will be irrevocably deleted.").build());

                    menuContainer.setItem(9*2 + 4, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("512ba5e0e3c9b54b5c29d7cc63c899086c27126d73623ed73be9492238dc7", "§8§l» §aAuto-Clear chat")
                            .addLore("§8§oThe chat is deleted every few minutes.").build());

                    /* todo:
                    menuContainer.setItem(9*2 + 5, new ItemBuilder(Material.KELP)
                            .setDisplayName("§8§l» §aAuto-Broadcast")
                            .addLore("§8§oIn the chat, automatic broadcasts pop up every few minutes.").build());
                     */

                    menuContainer.setItem(9*2 + 5, new ItemBuilder(Material.SOUL_TORCH)
                            .setDisplayName("§8§l» §bBroadcast")
                            .addLore("§8§oWrite a broadcast to all players on the server.").build());

                    menuContainer.setItem(9*2 + 6, new ItemBuilder(Material.WITHER_ROSE)
                            .setDisplayName("§8§l» §4Banned words")
                            .addLore("§8§oBan certain words").build());

                    menuContainer.setItem(9*2 + 7, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025", "§8§l» §c§lReset all chat settings")
                            .addLore("§8§oReset all chat settings to default.").build());

                });
    }

    @Override
    public Material material() {
        return Material.PAPER;
    }

    @Override
    public String name() {
        return "§8│ §fChatControl";
    }

    @Override
    public String realName() {
        return "chat_control";
    }

    @Override
    public String description() {
        return "Set different options for the chat.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.chat";
    }

    @Override
    public Boolean needPermission() {
        return false;
    }

    @Override
    public Boolean hasSkullID() {
        return false;
    }

    @Override
    public String getSkullID() {
        return null;
    }

    private void startAutoClearChatTask(final int delay) {
        clearChatTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitBootstrap.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers())
                for(int count = 0; count < 200; count++)
                    player.sendMessage("  ");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_clear_message"))));
        }, 60, delay* 1200L);
    }

    private void stopTask(final BukkitTask bukkitTask){
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getView().getTitle().equals("§8│ §f§lCHATCONTROL")){
                event.setCancelled(true);

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8§l» §cDisable chat":
                        configuration.set("chat_active", false);
                        saveConfig();

                        openInventory(player);
                        break;
                    case "§8§l» §aEnable chat":
                        configuration.set("chat_active", true);
                        saveConfig();

                        openInventory(player);
                        break;
                    case "§8§l» §9Change default chat color":
                        new ChatColorChanger(player);
                        break;
                    case "§8§l» §4Chat clear":
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                            for(int count = 0; count < 200; count++)
                                onlinePlayer.sendMessage("  ");

                        player.closeInventory();
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_clear_message"))));
                        break;
                    case "§8§l» §aAuto-Clear chat":
                        new AutoClearChat(player);
                        break;
                    case "§8§l» §bBroadcast":
                        new ServerModule.Broadcast(player).inventory();
                        break;
                    case "§8§l» §aAuto-Broadcast":
                        new AutoBroadCast(player);
                        break;
                    case "§8§l» §4Banned words":
                        new BannedWords(player);
                        break;
                    case "§8§l» §c§lReset all chat settings":

                        configuration.set("chat_active", true);
                        configuration.set("chat_color", ChatColor.WHITE.asBungee().name());
                        configuration.set("chat_clear_message", "&8| &4Chat has been cleared.");
                        configuration.set("autoclearchat.use", false);
                        configuration.set("autoclearchat.timer", 5);
                        configuration.set("chat_banned_words_punishment_action", "WARNING_MESSAGE");
                        configuration.set("chat_punish_message", "&cYou can't write that.");

                        saveConfig();
                        openInventory(player);

                        player.sendMessage(Base.PREFIX + "§4The chat settings have been reset.");
                        break;
                }

                return;
            }

            if(event.getView().getTitle().equals("§8§l» §fChatColor")){
                event.setCancelled(true);

                System.out.println(event.getCurrentItem().getItemMeta().getLore());

                if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                    for (ChatColor chatColor : ChatColor.values()) {
                        if (Objects.requireNonNull(event.getCurrentItem().getItemMeta().getLore()).get(0).equalsIgnoreCase(chatColor.asBungee().name())) {
                            configuration.set("chat_color", chatColor.asBungee().name());
                            saveConfig();

                            openInventory(player);

                            return;
                        }
                    }
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §cReset chat color")){
                    configuration.set("chat_color", ChatColor.WHITE.asBungee().name());
                    saveConfig();

                    openInventory(player);

                }
                return;
            }

            if(event.getView().getTitle().equals("§8§l» §aAuto-Broadcast")){
                event.setCancelled(true);

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8§l» §cDisable module":
                        configuration.set("autoclearchat.use", false);
                        saveConfig();

                        new AutoClearChat(player);

                        if(clearChatTaskID != null)
                            stopTask(clearChatTaskID);
                        break;
                    case "§8§l» §aActivate module":
                        configuration.set("autoclearchat.use", true);
                        saveConfig();

                        new AutoClearChat(player);

                        if(clearChatTaskID != null)
                            stopTask(clearChatTaskID);

                        startAutoClearChatTask(configuration.getInt("autoclearchat.timer"));
                        break;
                    case "§8§l» §3Every 1 minute(s)": case "§8§l» §3Every 5 minute(s)": case "§8§l» §3Every 10 minute(s)": case "§8§l» §3Every 15 minute(s)":
                        case "§8§l» §3Every 20 minute(s)": case "§8§l» §3Every 25 minute(s)": case "§8§l» §3Every 30 minute(s)": case "§8§l» §3Every 1 hour(s)":
                            final int timerMinutes = Integer.parseInt(Objects.requireNonNull(event.getCurrentItem().getItemMeta().getLore()).get(0));

                            configuration.set("autoclearchat.timer", timerMinutes);
                            saveConfig();
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 75F, 75F);

                            new AutoClearChat(player);

                            if(configuration.getBoolean("autoclearchat.use")){
                                if(clearChatTaskID != null)
                                    stopTask(clearChatTaskID);

                                startAutoClearChatTask(timerMinutes);
                                return;
                            }
                        break;
                    case "§8§l» §eChange clear chat message":
                        playersWhoChangeClearChatMessage.add(player.getUniqueId());

                        player.closeInventory();
                        player.sendMessage(Base.PREFIX + "§7Write the new message in the chat.");
                        player.sendMessage(Base.PREFIX + "§8§oYou can cancel the process by writing §c\"cancel\"§8§o in the chat.");

                        break;
                }
                return;
            }

            if(event.getView().getTitle().equals("§8§l» §aAuto-Broadcast")){
                event.setCancelled(true);
                return;
            }

            if(event.getView().getTitle().equals("§8§l» §4Banned words")){
                event.setCancelled(true);
                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§7• §4")){
                    final String word = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§7• §4", "");

                    BannedWords.openSubMenu(player, word);
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §4Punishment")){
                    switch (Objects.requireNonNull(configuration.getString("chat_banned_words_punishment_action"))){
                        case "WARNING_MESSAGE":
                            configuration.set("chat_banned_words_punishment_action", "WARNING_ACTIONBAR");
                            saveConfig();

                            new BannedWords(player);
                            break;
                        case "WARNING_ACTIONBAR":
                            configuration.set("chat_banned_words_punishment_action", "WARNING_TITLE");
                            saveConfig();

                            new BannedWords(player);
                            break;
                        case "WARNING_TITLE":
                            configuration.set("chat_banned_words_punishment_action", "KICK");
                            saveConfig();

                            new BannedWords(player);
                            break;
                        case "KICK":
                            configuration.set("chat_banned_words_punishment_action", "WARNING_MESSAGE");
                            saveConfig();

                            new BannedWords(player);
                            break;
                    }
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §eChange punishment message")){
                    playersWhoChangePunishChatMessage.add(player.getUniqueId());

                    player.closeInventory();
                    player.sendMessage("  ");
                    player.sendMessage(Base.PREFIX + "§7Please post the new Punish message in the chat.");
                    player.sendMessage(Base.PREFIX + "§7You can cancel the process by writing §c\"cancel\"§7 in the chat.");
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §aAdd word")){
                    playersWhoCreateBannedWord.add(player.getUniqueId());

                    player.closeInventory();
                    player.sendMessage("  ");
                    player.sendMessage(Base.PREFIX + "§7Please write the word/phrase you want to be blocked in the chat.");
                    player.sendMessage(Base.PREFIX + "§7You can cancel the process by writing §c\"cancel\"§7 in the chat.");
                }

                return;
            }

            if(event.getView().getTitle().equals("§8§l» §4Word settings")){
                event.setCancelled(true);

                final String word = event.getView().getItem(0).getItemMeta().getLore().get(0).replaceAll("§8• §c", "");
                final List<String> list = configuration.getStringList("chat_banned_words_list");

                if(!list.contains(word)) {
                    player.closeInventory();
                    new BannedWords(player);
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §4Delete word")){
                    list.remove(word);

                    configuration.set("chat_banned_words_list", list);
                    saveConfig();
                    new BannedWords(player);

                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8§l» §eEdit word")){
                    list.remove(word);

                    configuration.set("chat_banned_words_list", list);
                    saveConfig();

                    playersWhoCreateBannedWord.add(player.getUniqueId());

                    player.sendMessage(Base.PREFIX + "§7Write the new word in the chat.");
                    player.sendMessage(Base.PREFIX + "§8§oYou can cancel the process by writing §c\"cancel\"§8§o in the chat.");
                    player.closeInventory();

                    return;
                }
            }
        }
    }

    public class PlayerChatListener implements Listener {

        @EventHandler
        public void handle(final PlayerChatEvent event){
            final Player player = event.getPlayer();
            final String message = event.getMessage();
            final ChatColor chatColor = ChatColor.valueOf(configuration.getString("chat_color"));

            if(playersWhoCreateBannedWord.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playersWhoCreateBannedWord.remove(player.getUniqueId());
                    new BannedWords(player);
                    return;
                }

                final List<String> list = configuration.getStringList("chat_banned_words_list");

                list.add(event.getMessage());

                configuration.set("chat_banned_words_list", list);
                saveConfig();

                playersWhoCreateBannedWord.remove(player.getUniqueId());
                new BannedWords(player);

                player.sendMessage(Base.PREFIX + "§aYou have successfully locked the word/phrase.");
            }

            if(playersWhoChangePunishChatMessage.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playersWhoChangePunishChatMessage.remove(player.getUniqueId());
                    new BannedWords(player);
                    return;
                }

                configuration.set("chat_punish_message", event.getMessage());
                saveConfig();

                playersWhoChangePunishChatMessage.remove(player.getUniqueId());
                new BannedWords(player);

                return;
            }

            if(playersWhoChangeClearChatMessage.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playersWhoChangeClearChatMessage.remove(player.getUniqueId());
                    new AutoClearChat(player);
                    return;
                }

                configuration.set("chat_clear_message", event.getMessage());
                saveConfig();

                playersWhoChangeClearChatMessage.remove(player.getUniqueId());

                new AutoClearChat(player);

                return;
            }

            if(!configuration.getBoolean("chat_active")) {
                event.setCancelled(true);
                return;
            }

            for(String bannedWords : configuration.getStringList("chat_banned_words_list")) {
                if (event.getMessage().equalsIgnoreCase(bannedWords)) {

                    switch (Objects.requireNonNull(configuration.getString("chat_banned_words_punishment_action"))) {
                        case "WARNING_MESSAGE":
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_punish_message"))));
                            break;
                        case "WARNING_ACTIONBAR":
                            event.setCancelled(true);

                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                                    ('&', Objects.requireNonNull(configuration.getString("chat_punish_message")))));
                            break;
                        case "WARNING_TITLE":
                            event.setCancelled(true);
                            player.sendTitle("", ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_punish_message"))));

                            break;
                        case "KICK":
                            event.setCancelled(true);

                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_punish_message"))));
                            break;
                    }
                }
            }

            event.setMessage(chatColor + message);
        }
    }

    public static class BannedWords {

        private final Player player;

        public BannedWords(Player player) {
            this.player = player;
            open();
        }

        private void open(){
            new ScrollableMenu("§8§l» §4Banned words", 9*6, 9, 9*4, 53, 9*5 + 1, 9*5,
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76", "§7§l✖ §8┃ §cNo more pages found.").addLore("§8§oThere are no other pages.").build(),
                    new ItemBuilder(Material.REDSTONE).build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be", "§7§l» §8┃ §eNext page").build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d", "§7§l« §8┃ §ePage back").build())
                    .open(player, menuContainer -> {
                        setGlass(menuContainer);

                        for(String blackWords : configuration.getStringList("chat_banned_words_list"))
                            menuContainer.addSortItem(new ItemBuilder(Material.PLAYER_HEAD)
                                    .setSkullTexture("7658ccc7345559e9321f49ee1af67522e708dca8932b0a721cc34130731eb598", "§7• §4" + blackWords)
                                    .addLore("§8§oClick for more options").build());

                        menuContainer.setItem(4, new ItemBuilder(Material.PAPER)
                                .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                                .addLore("§8› §7ChatControl §8| §7Banned words §8‹")
                                .build());

                        menuContainer.setItem(9*5 + 3, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716", "§8§l» §aAdd word").build());

                        menuContainer.setItem(9*5 + 5, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("a2629f2682dcee30f5855b1e5427cc4bee73d18a276fafc520d693b40ca81b22", "§8§l» §eChange punishment message")
                                .addLore("§8‹ §r" + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_punish_message"))) + " §8›").build());

                        menuContainer.setItem(9*5 + 6, new ItemBuilder(Material.PLAYER_HEAD)

                                .setSkullTexture("3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025", "§8§l» §4Punishment")
                                .addLore("§8§oCurrent setting: §7§l" + configuration.getString("chat_banned_words_punishment_action")).build());

                    });
        }

        public static void openSubMenu(final Player player, final String word){
            new Menu("§8§l» §4Word settings", 9, 8, new ItemBuilder(Material.REDSTONE)
                    .setDisplayName("§aBack").build())
                    .open(player, menuContainer -> {
                        menuContainer.setItem(0, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("7658ccc7345559e9321f49ee1af67522e708dca8932b0a721cc34130731eb598", "§8• §c" + word)
                                .addLore(word).build());

                        menuContainer.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build());

                        menuContainer.setItem(2, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("38f50655a00f76a39996a3b4a100420102e8473da9a4223dc1894f96ba7fdae5", "§8§l» §eEdit word")
                                .addLore("§8§oClick to edit the word.").build());

                        menuContainer.setItem(3, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025", "§8§l» §4Delete word").build());

                    });

        }

        private void setGlass(final MenuContainer menuContainer){
            for (int i = 0; i < 9; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            for (int i = 9*4; i < 9*6; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
        }
    }

    public class ChatColorChanger {

        private final Player player;

        public ChatColorChanger(Player player){
            this.player = player;

            open();
        }

        private void open(){
            new Menu("§8§l» §fChatColor", 9*3).open(player, menuContainer -> {
                for(int count = 0; count < 9*3; count++)
                    menuContainer.setItem(count, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build());

                menuContainer.setItem(9, new ItemBuilder(Material.PAPER).setDisplayName("§8§l» §fChoose a chat colour").setLore("§8§oClick on the colour you want to have as the chat colour.").build());
                menuContainer.setItem(9 + 1, new ItemBuilder(Material.PLAYER_HEAD)
                        .setSkullTexture("1919d1594bf809db7b44b3782bf90a69f449a87ce5d18cb40eb653fdec2722", "§8§l» §cReset chat color").setLore("§8§oThe chat colour will be reset.").build());

                menuContainer.setItem(3, itemColorWithDefined(ChatColor.BLACK, "cfa4dda6d19a1fe2d988d65dec53429505308166c9067b68a4770ca5c436cf94"));
                menuContainer.setItem(4, itemColorWithDefined(ChatColor.GOLD, "adf2eb205a23c1196b3ecf21e68c076b696e76163ac8fc4fb9f5318c2a5e5b1a"));
                menuContainer.setItem(5, itemColorWithDefined(ChatColor.DARK_RED, "6953b12a0946b629b4c0889d41fd26ed26fb729d4d514b59727124c37bb70d8d"));
                menuContainer.setItem(6, itemColorWithDefined(ChatColor.BLUE, "163e6646f1c0d41fd3bf5584a1ce044f5c46d598258db46216117859f57af197"));
                menuContainer.setItem(7, itemColorWithDefined(ChatColor.YELLOW, "4d905269accab24b11924eba8bd92991b8d85ce4276027a1636c931b6d06c89e"));
                menuContainer.setItem(12, itemColorWithDefined(ChatColor.GREEN, "77472d608821f45a8805376ec0c6ffcb78117829ea5f960041c2a09d10e04cb4"));
                menuContainer.setItem(13, itemColorWithDefined(ChatColor.LIGHT_PURPLE, "89ec5a30222d0659b0dbee844b8f53eae62fe95b4a3448a9ef790a7aedb296d9"));
                menuContainer.setItem(14, itemColorWithDefined(ChatColor.GRAY, "55288ddc911a75f77c3a5d336365a8f8b139fa53930b4b6ee139875c80ce366c"));
                menuContainer.setItem(15, itemColorWithDefined(ChatColor.DARK_GRAY, "adf21f532122566af893da27880a1b6095c35712f29a378cfecc7fe2b1328ab4"));
                menuContainer.setItem(16, itemColorWithDefined(ChatColor.AQUA, "d83288620617bd5cedc8fdb7133cfad231ce25c13cb8726bbf76e5c72fe732ab"));
                menuContainer.setItem(21, itemColorWithDefined(ChatColor.DARK_PURPLE, "f97012ed6a92b05ea0f194950748544e075baa28781ca373d1b27e28c26953c"));
                menuContainer.setItem(22, itemColorWithDefined(ChatColor.DARK_BLUE, "ea81fcb51be2a9f89b1adc9d87239ba429d635fbe01b37ec329164887bf665b"));
                menuContainer.setItem(23, itemColorWithDefined(ChatColor.DARK_GREEN, "53581c2f9cf358d7edc78dd6fd4b6257501bc4e6455e33fa0caae207cf0321a2"));
                menuContainer.setItem(24, itemColorWithDefined(ChatColor.RED, "6953b12a0946b629b4c0889d41fd26ed26fb729d4d514b59727124c37bb70d8d"));
                menuContainer.setItem(25, itemColorWithDefined(ChatColor.DARK_AQUA, "ea81fcb51be2a9f89b1adc9d87239ba429d635fbe01b37ec329164887bf665b"));

            });
        }

        private ItemStack itemColorWithDefined(final ChatColor chatColor, final String headID){
            return new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture(headID, "§b§r§8» " + (chatColor) + chatColor.asBungee().name()).addLore(chatColor.asBungee().name(), "§8§oClick to change the chat colour.").build();
        }
    }

    public class AutoBroadCast {

        private final Player player;

        public AutoBroadCast(Player player) {
            this.player = player;

            open();
        }

        private void open(){
            new ScrollableMenu("§8§l» §aAuto-Broadcast", 9*6, 9, 9*4, 53, 9*5 + 1, 9*5,
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76", "§7§l✖ §8┃ §cNo more pages found.").addLore("§8§oThere are no other pages.").build(),
                    new ItemBuilder(Material.REDSTONE).build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("d4be8aeec11849697adc6fd1f189b16642dff19f2955c05deaba68c9dff1be", "§7§l» §8┃ §eNext page").build(),
                    new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3625902b389ed6c147574e422da8f8f361c8eb57e7631676a72777e7b1d", "§7§l« §8┃ §ePage back").build())

                    .open(player, menuContainer -> {
                        for (int i = 0; i < 9; i++)
                            menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                        for (int i = 9*4; i < 9*6; i++)
                            menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                        menuContainer.setItem(4, new ItemBuilder(Material.PAPER)
                                .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                                .addLore("§8› §7ChatControl §8| §7AutoBroadcast menu §8‹")
                                .build());

                        menuContainer.setItem(51, new ItemBuilder(Material.LIME_DYE).setDisplayName("§8§l» §aEnable module")
                                .addLore("§8§oWhen you activate the module, the AutoBroadcaster starts.")
                                .build());

                        menuContainer.setItem(48, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716", "§8§l» §eCreate an automatic broadcast")
                                .addLore("§8§oCreate another broadcast message that appears in the chat every few minutes.")
                                .build());


                    });
        }
    }

    public class AutoClearChat {

        private final Player player;

        public AutoClearChat(Player player) {
            this.player = player;

            this.open();
        }

        private void open(){
            new Menu("§8§l» §aAuto-Broadcast", 9*6, 53, new ItemBuilder(Material.REDSTONE)
                    .setDisplayName("§aBack").build())
                    .open(player, menuContainer -> {
                        setGlass(menuContainer);

                        menuContainer.setItem(4, new ItemBuilder(Material.PAPER)
                                .setDisplayName("§8§l┃ §6§lSERVER MANAGER §8§l┃")
                                .addLore("§8› §7ChatControl §8| §7AutoClearChat menu §8‹")
                                .build());
                        menuContainer.setItem(9*2, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("512ba5e0e3c9b54b5c29d7cc63c899086c27126d73623ed73be9492238dc7", "§8§l» §aAuto-Clear chat")
                                .addLore("  ", "§8| §6Timer §8» §7" + configuration.getInt("autoclearchat.timer") + " minute(s)", "§8| §eMode §8» §7" + configuration.getBoolean("autoclearchat.use")).build());
                        menuContainer.setItem(9*2 + 2, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("5f1fec3b183c0fd617353b7fdb7fc8afb3f7a6ef55652a2e96e3925f72fc78a0", "§8§l» §eChange clear chat message")
                                .addLore("§8§oThis message is sent automatically when the chat is cleared."," ","§8§oCurrent message: ", "§8‹ §r" + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString("chat_clear_message")) + " §8›")).build());

                        if(!configuration.getBoolean("autoclearchat.use"))
                            menuContainer.setItem(9*2 + 3, new ItemBuilder(Material.LIME_DYE).setDisplayName("§8§l» §aActivate module")
                                    .addLore("§8§oWhen you click on activate, the timer is started and", "§8§othe chat starts to delete itself every few minutes.").build());
                        else
                            menuContainer.setItem(9*2 + 3, new ItemBuilder(Material.GRAY_DYE).setDisplayName("§8§l» §cDisable module")
                                    .addLore("§8§oIf you click on disable, the module is disabled.").build());

                        menuContainer.setItem(14, new ItemBuilder(Material.PLAYER_HEAD).setLore("1").setSkullTexture("71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530", "§8§l» §3Every 1 minute(s)").build());
                        menuContainer.setItem(15, new ItemBuilder(Material.PLAYER_HEAD).setLore("5").setSkullTexture("6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2", "§8§l» §3Every 5 minute(s)").build());
                        menuContainer.setItem(16, new ItemBuilder(Material.PLAYER_HEAD).setLore("10").setSkullTexture("49dd36759307db8e2d9f4b0c2aed2556db1ddcf3f67fa19cc826acbd965fe", "§8§l» §3Every 10 minute(s)").build());
                        menuContainer.setItem(17, new ItemBuilder(Material.PLAYER_HEAD).setLore("15").setSkullTexture("f8fb6f724563ff82a83ab17edb9b8aba1d28b5b324444057fecda5b167cd2", "§8§l» §3Every 15 minute(s)").build());
                        menuContainer.setItem(32, new ItemBuilder(Material.PLAYER_HEAD).setLore("20").setSkullTexture("344b319b2543b0aef16bae925fecc41b574dbebdb164830a05deea3ec97fbe", "§8§l» §3Every 20 minute(s)").build());
                        menuContainer.setItem(33, new ItemBuilder(Material.PLAYER_HEAD).setLore("25").setSkullTexture("8fb46c3a72847d7fac24744f5d8fb56b6c3bb7258fcbd380a2a02eb9680ac", "§8§l» §3Every 25 minute(s)").build());
                        menuContainer.setItem(34, new ItemBuilder(Material.PLAYER_HEAD).setLore("30").setSkullTexture("6b2e31624f8e5186e9fa8c7ad112ec83faddb5a85f9f9c32d7f9e84871ae0649", "§8§l» §3Every 30 minute(s)").build());
                        menuContainer.setItem(35, new ItemBuilder(Material.PLAYER_HEAD).setLore("60").setSkullTexture("71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530", "§8§l» §3Every 1 hour(s)").build());


            });
        }

        private void setGlass(final MenuContainer menuContainer){
            for (int i = 0; i < 9; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            for (int i = 9*4; i < 9*6; i++)
                menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

            menuContainer.setItem(10, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(13, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(19, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(22, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(23, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(24, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(25, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(26, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(28, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
            menuContainer.setItem(31, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

        }
    }
}
