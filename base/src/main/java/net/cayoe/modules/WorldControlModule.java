package net.cayoe.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.menu.ScrollableMenu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class WorldControlModule extends Module {

    private static final HashMap<Player, WorldSetup> playerWorldSetupHashMap = Maps.newHashMap();
    private static final List<UUID> playersImportSetupList = Lists.newArrayList();

    @Override
    public void onLoad() {
        Base.registerEvents(new InventoryClickListener());
        Base.registerEvents(new PlayerChatListener());
    }

    @Override
    public void openInventory(Player player) {
        new ScrollableMenu("§7§l┃ §b§lWORLDCONTROL", 9*6, 9, 9*4, 9*5+8, 9*5 + 1, 9*5,
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

            menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullTexture("438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                    .addLore("§8› §7WorldControl §8‹")
                    .build());

            menuContainer.setItem(9*5 + 3, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716", "§8┃ §aCreate world").build());
            menuContainer.setItem(9*5 + 4, new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("3c051ad994b2ea1be9df3dbc4bb6254ebd96201c7946d08ae1eac42141c0c17a", "§8┃ §3Import world").build());

            for(World world : Bukkit.getWorlds()){
                menuContainer.addSortItem(new ItemBuilder(Material.GRASS_BLOCK)
                        .setDisplayName("§8» §a" + world.getName())
                        .addLore("§7Right/Left click to show more info...").build());
            }

        });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String realName() {
        return "world_control";
    }

    @Override
    public String name() {
        return "§8│ §bWorldControl";
    }

    @Override
    public String description() {
        return "Create worlds, delete worlds, import worlds and more.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.world";
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
        return "438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc";
    }

    public static class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            if(event.getView().getTitle().equals("§7§l┃ §b§lWORLDCONTROL")){
                event.setCancelled(true);

                if(Base.getModuleHandler().getModule("world_control").needPermission())
                    if(!player.hasPermission(Base.getModuleHandler().getModule("world_control").permission()))
                        return;

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8┃ §aCreate world")){
                    player.sendMessage(Base.PREFIX + "§8§oWorld creation setup has been started...");
                    player.closeInventory();
                    player.sendMessage("   ");
                    new WorldSetup(player);

                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8┃ §3Import world")){
                    playersImportSetupList.add(player.getUniqueId());

                    player.closeInventory();
                    player.sendMessage(Base.PREFIX + "§7Please write the folder of the world you want to import in the chat.");
                    player.sendMessage(Base.PREFIX + "§7You can always use '§ccancel§7' to quit setup.");

                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§8» §a")){
                    if(event.getCurrentItem().getType().equals(Material.GRASS_BLOCK) ||
                            event.getCurrentItem().getType().equals(Material.NETHERRACK) ||
                            event.getCurrentItem().getType().equals(Material.END_STONE)){
                        new WorldSpecifInventory(event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§8» §a", ""), player);
                        return;
                    }

                }
                return;
            }

            if(event.getView().getTitle().startsWith("§r§8» §a")) {
                final World world = Bukkit.getWorld(event.getView().getTitle().replaceAll("§r§8» §a", ""));

                event.setCancelled(true);
                if (Base.getModuleHandler().getModule("world_control").needPermission()){
                    if(!player.hasPermission(Base.getModuleHandler().getModule("world_control").permission()))
                        return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§8│ §cPVP §8» §7")){
                    if(world.getPVP())
                        world.setPVP(false);
                    else
                        world.setPVP(true);

                    Base.getModuleHandler().getModule("world_control").openInventory(player);
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§8│ §eTime §8» §7")){
                    if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("NIGHT"))
                        Objects.requireNonNull(world).setTime(6000);
                    else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("DAY"))
                        Objects.requireNonNull(world).setTime(12999);

                    Base.getModuleHandler().getModule("world_control").openInventory(player);
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8│ §3Teleport to world")){
                    player.teleport(Objects.requireNonNull(world).getSpawnLocation());
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8│ §2Change world spawn point")){
                    player.closeInventory();

                    if(!player.getWorld().equals(world)){
                        player.sendMessage(Base.PREFIX + "§cYou are not in this world.");
                        return;
                    }

                    world.setSpawnLocation(player.getLocation());
                    player.sendMessage(Base.PREFIX + "§7You have successfully changed the spawn location.");
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§8│ §cDelete world")){
                    player.closeInventory();

                    if(Objects.requireNonNull(world).getName().equals("world")) {
                        player.sendMessage(Base.PREFIX + "§cYou cannot delete the main world!");
                        return;
                    }

                    player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
                    player.sendMessage(Base.PREFIX + "§cAn attempt is being made to erase the world once and for all.");

                    Bukkit.getServer().unloadWorld(world, false);
                    world.getWorldFolder().delete();

                    player.sendMessage(Base.PREFIX + "§eAn attempt was made to erase the world...");

                    return;
                }

                return;
            }

            if(event.getView().getTitle().equals("§8» §bChose a world type")){
                final WorldSetup worldSetup = playerWorldSetupHashMap.get(player);

                switch (event.getCurrentItem().getType()){
                    case GRASS_BLOCK:
                        worldSetup.setWorldType(World.Environment.NORMAL);
                        worldSetup.nextStep(player);
                        break;
                    case NETHERRACK:
                        worldSetup.setWorldType(World.Environment.NETHER);
                        worldSetup.nextStep(player);
                        break;
                    case DIRT:
                        worldSetup.setBetWorldTyp(WorldType.FLAT);
                        worldSetup.nextStep(player);
                        break;
                    case END_STONE:
                        worldSetup.setWorldType(World.Environment.THE_END);
                        worldSetup.nextStep(player);
                        break;
                }
                player.closeInventory();
            }
        }
    }

    public static class PlayerChatListener implements Listener{

        @EventHandler
        public void handle(final PlayerChatEvent event){
            final Player player = event.getPlayer();

            if(playersImportSetupList.contains(player.getUniqueId())){
                event.setCancelled(true);

                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playersImportSetupList.remove(player.getUniqueId());
                    Base.getModuleHandler().getModule("world_control").openInventory(player);
                    return;
                }

                player.sendMessage(Base.PREFIX + "§8§oTry to import the world...");
                player.sendMessage(" ");

                if(!new File(System.getProperty("user.dir") + "/" + event.getMessage()).exists()) {
                    player.sendMessage(Base.PREFIX + "§cThis folder was not found.");
                    return;
                }
                if(Bukkit.getWorld(event.getMessage()) != null){
                    player.sendMessage(Base.PREFIX + "§cThis world already exists.");
                    return;
                }

                Bukkit.createWorld(new WorldCreator(event.getMessage()));

                player.sendMessage(Base.PREFIX + "§aThe world has been imported.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 45F, 45F);

                return;
            }

            if(playerWorldSetupHashMap.containsKey(player)){
                if(event.getMessage().equalsIgnoreCase("cancel")){
                    playerWorldSetupHashMap.remove(player.getUniqueId());
                    Base.getModuleHandler().getModule("world_control").openInventory(player);
                    return;
                }

                event.setCancelled(true);
                final WorldSetup worldSetup = playerWorldSetupHashMap.get(player);

                if(event.getMessage().equalsIgnoreCase("cancel")) {
                    worldSetup.exit(player);
                    player.sendMessage(Base.PREFIX + "§cThe process was canceled.");
                    return;
                }

                if(worldSetup.getCurrentStep() == 1){
                    worldSetup.setWorldName(event.getMessage());
                    worldSetup.nextStep(player);
                    return;
                }

                if(worldSetup.getCurrentStep() == 3){
                    if(event.getMessage().equalsIgnoreCase("0")){
                        worldSetup.nextStep(player);
                        return;
                    }

                    try {
                        worldSetup.setSeed(Integer.parseInt(event.getMessage()));
                        worldSetup.nextStep(player);
                    }catch (NumberFormatException exception) {
                        worldSetup.exit(player);
                        player.sendMessage(Base.PREFIX + "§4§lError: §cThe process was canceled.");
                    }
                }
            }
        }

    }

    public static class WorldSetup {

        private String worldName;
        private World.Environment worldType;

        private WorldType betWorldTyp;

        private int seed;
        private int currentStep;

        private final Player player;

        public WorldSetup(final Player player){
            this.player = player;

            player.sendMessage(Base.PREFIX + "§7You can always use '§ccancel§7' to quit setup.");
            player.sendMessage(Base.PREFIX + "§7Please write the name of the world in the chat.");

            this.currentStep = 1;
            this.seed = 0;
            this.worldType = World.Environment.NORMAL;
            this.betWorldTyp = null;

            playerWorldSetupHashMap.put(player, this);
        }

        public void nextStep(final Player player){
            setStep(getCurrentStep() + 1);

            switch (currentStep){
                case 1:
                    player.sendMessage(Base.PREFIX + "§7Please choose the world name.");
                    player.sendMessage(Base.PREFIX + "§7Write §c\"cancel\"§7 to cancel.");
                    break;
                case 2:
                    openChoseWorldInventory(player);
                    break;
                case 3:
                    player.closeInventory();
                    player.sendMessage(Base.PREFIX + "§7Please choose a seed number, if you don't want one write \"0\"§7.");
                    player.sendMessage(Base.PREFIX + "§7Write §c\"cancel\"§7 to cancel.");
                    break;
                case 4:
                    player.closeInventory();
                    player.sendMessage(Base.PREFIX + "§aThe process is complete, your world is now being created...");
                    player.sendMessage(Base.PREFIX + "§7Please wait a moment...");

                    if(worldType == null){
                        if(seed == 0)
                            Bukkit.createWorld(new WorldCreator(worldName).environment(worldType));
                        else
                            Bukkit.createWorld(new WorldCreator(worldName).environment(worldType).seed(seed));
                    }else {
                        if(seed == 0)
                            Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT));
                        else
                            Bukkit.createWorld(new WorldCreator(worldName).type(WorldType.FLAT).seed(seed));
                    }

                    player.sendMessage(Base.PREFIX + "§aYour new world has been created.");
                    player.sendMessage(Base.PREFIX + "§7Name: " + worldName);
                    player.sendMessage(Base.PREFIX + "§7Type: " + worldType.toString());
                    if(seed != 0)
                        player.sendMessage(Base.PREFIX + "§7Seed: " + seed);

                    exit(player);
                    break;
            }

            newSet();
        }

        public void exit(final Player player){
            playerWorldSetupHashMap.remove(player);
        }

        private void openChoseWorldInventory(final Player player){
            final Inventory container = Bukkit.createInventory(null, 9, "§8» §bChose a world type");

            container.setItem(2, new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("§8» §2Normal world").build());
            container.setItem(3, new ItemBuilder(Material.DIRT).setDisplayName("§8» §2Flat world").build());
            container.setItem(4, new ItemBuilder(Material.NETHERRACK).setDisplayName("§8» §2Nether world").build());
            container.setItem(5, new ItemBuilder(Material.END_STONE).setDisplayName("§8» §2End world").build());

            player.openInventory(container);
        }

        private void newSet(){
            exit(player);
            playerWorldSetupHashMap.put(player, this);
        }

        private void setStep(final int step){
            this.currentStep = step;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        public WorldType getBetWorldTyp() {
            return betWorldTyp;
        }

        public void setSeed(int seed) {
            this.seed = seed;
            newSet();
        }

        public void setWorldName(String worldName) {
            this.worldName = worldName;
            newSet();
        }

        public void setWorldType(World.Environment worldType) {
            this.worldType = worldType;
            newSet();
        }

        public void setBetWorldTyp(WorldType betWorldTyp) {
            this.betWorldTyp = betWorldTyp;
        }

        public int getSeed() {
            return seed;
        }

        public String getWorldName() {
            return worldName;
        }

        public World.Environment getWorldType() {
            return worldType;
        }
    }

    public static class WorldSpecifInventory {

        private final Player player;

        public WorldSpecifInventory(final String worldName, Player player) {
            this.player = player;

            this.openMenu(worldName);
        }

        private void openMenu(final String worldName){
            final World world = Bukkit.getWorld(worldName);

            new Menu("§r§8» §a" + worldName, 9, 8,
                    new ItemBuilder(Material.REDSTONE).setDisplayName("§7§l« §8┃ §eBack").build())
                    .open(player, menuContainer -> {
                        menuContainer.setItem(0, new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullTexture("438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc", "§r§8│ §a" + worldName)
                                .setLore("",
                                        "§8» §7World type: " + world.getWorldType(),
                                        "§8» §7PVP: " + world.getPVP(),
                                        "§8» §7Seed: " + world.getSeed(),
                                        "§8» §7Players in world: " + world.getPlayers().size(),
                                        "§8» §7Full time: " + world.getFullTime()).build());
                        menuContainer.setItem(1, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build());

                        menuContainer.setItem(2, new ItemBuilder(Material.NETHERITE_SWORD).setDisplayName("§8│ §cPVP §8» §7" + world.getPVP())
                                .setLore("§8§oClick to switch PVP on or off.")
                                .build());

                        if(world.getFullTime() > 12999)
                            menuContainer.setItem(3, new ItemBuilder(Material.CLOCK).setDisplayName("§8│ §eTime §8» §7" + "NIGHT")
                                    .setLore("§8§oClick to switch time to day or night")
                                    .build());
                        else if(world.getFullTime() > 5999)
                            menuContainer.setItem(3, new ItemBuilder(Material.CLOCK).setDisplayName("§8│ §eTime §8» §7" + "DAY")
                                    .setLore("§8§oClick to switch time to day or night")
                                    .build());
                        menuContainer.setItem(4, new ItemBuilder(Material.SOUL_TORCH).setDisplayName("§8│ §2Change world spawn point")
                                .setLore("§8§oClick to change the spawn point of the world")
                                .build());
                        menuContainer.setItem(5, new ItemBuilder(Material.ENDER_PEARL).setDisplayName("§8│ §3Teleport to world")
                                .setLore("§8§oClick to teleport to world")
                                .build());
                            menuContainer.setItem(7, new ItemBuilder(Material.BARRIER).setDisplayName("§8│ §cDelete world")
                                    .setLore("§8§oClick to delete the world", "§c§oWARNING: §7The world is permanently deleted", "§7after the click and cannot be allowed again.")
                                    .build());

            });
        }

        //        Toolkit toolkit = Toolkit.getDefaultToolkit();
        //        Clipboard clipboard = toolkit.getSystemClipboard();
        //        StringSelection strSel = new StringSelection("test text");
        //
        //        clipboard.setContents(strSel, null);

    }

}
