package net.cayoe.modules;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import net.cayoe.Base;
import net.cayoe.utils.ItemBuilder;
import net.cayoe.utils.menu.Menu;
import net.cayoe.utils.module.Module;
import net.cayoe.utils.player.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class RealisticWeatherModule extends Module {

    private File file = new File("plugins/ServerManager/realistic_weather.yml");
    private static YamlConfiguration configuration;

    public static OpenWeatherMapClient openWeatherMapClient;

    @Override
    public void onLoad() {
        Base.registerEvents(new InventoryClickListener());

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.get("realistic_weather_use") == null)
            configuration.set("realistic_weather_use", false);
        if(configuration.get("location") == null)
            configuration.set("location", "London");
        if(configuration.get("key") == null)
            configuration.set("key", "NULL");

        saveConfig();

       openWeatherMapClient =  new OpenWeatherMapClient(configuration.getString("key"));
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
        new Menu("§8§l» §e§lRealistic weather", 9*6, 53, new ItemBuilder(Material.REDSTONE)
                .setDisplayName("§aBack").build())
                .open(player, menuContainer -> {
                    for (int i = 0; i < 9; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    for (int i = 9*4; i < 9*6; i++)
                        menuContainer.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                    menuContainer.setItem(9, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(10, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(11, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(20, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(27, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(28, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("  ").build());
                    menuContainer.setItem(29, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("  ").build());

                    menuContainer.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullTexture("b7d845a35a56684a742fec339f50272cc145bee363bec81a80d6ef1f748ea61b", "§8§l┃ §6§lSERVER MANAGER §8§l┃")
                            .addLore("§8› §7Realistic weather §8‹")
                            .build());

                    if(!configuration.getBoolean("realistic_weather_use"))
                        menuContainer.setItem(19, new ItemBuilder(Material.LIME_DYE).setDisplayName("§8§l» §aEnable Module")
                                .addLore("§8§oWhen the module is activated,", "§8§othe weather changes as in real life.")
                                .build());
                    else
                        menuContainer.setItem(19, new ItemBuilder(Material.GRAY_DYE).setDisplayName("§8§l» §cDisable Module")
                                .addLore("§8§oClick to deactivate the module with its functions")
                                .build());

                    if(!Objects.requireNonNull(configuration.getString("key")).equalsIgnoreCase("NULL"))
                        menuContainer.setItem(18, new ItemBuilder(Material.MAP).setDisplayName("§8§l» §eChange location")
                            .addLore("§8§oClick to change the location by name", " ", "§8| §6Location §8» §7" + getLocation(configuration.getString("location")),
                                    "§8| §eCurrent §8» §a§l" + getLocation(configuration.getString("location")).getWeather())
                            .build());
                    else
                        menuContainer.setItem(18, new ItemBuilder(Material.MAP).setDisplayName("§8§l» §eChange location")
                                .addLore("§c§oNo key was found")
                                .build());

                    menuContainer.setItem(52, new ItemBuilder(Material.PAPER).setDisplayName("§8§l» §eOther city").addLore("§8§oDefine the city yourself").build());

                    for (Location value : Location.values()) {
                        menuContainer.addItem(new ItemBuilder(Material.LECTERN).setDisplayName("§7§8| §2" + value.getName()).addLore(" ", "§e§o" + value.getWeather() ,"§8§o" + value.getCountry(), " ").build());
                    }
                });
    }

    @Override
    public Material material() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public String name() {
        return "§8│ §e§lRealistic weather";
    }

    @Override
    public String realName() {
        return "realistic_weather_module";
    }

    @Override
    public String description() {
        return "The weather from the real world is adopted in Minecraft.";
    }

    @Override
    public String permission() {
        return "server-manager.modules.realistic_weather";
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
        return "b7d845a35a56684a742fec339f50272cc145bee363bec81a80d6ef1f748ea61b";
    }

    private Location getLocation(final String typ){
        for (Location location : Location.values()) {
            if(location.name().equals(typ))
                return location;
        }
        return Location.LONDON;
    }

    public static OpenWeatherMapClient getOpenWeatherMapClient() {
        return openWeatherMapClient;
    }

    private void confirmWeather(){
        for (World world : Bukkit.getWorlds()) {

        }
    }

    public enum Location {
        LONDON("London", "England"),
        MADRID("Madrid", "Spain"),
        PARIS("Paris", "France"),
        BERLIN("Berlin", "Germany"),
        WARSAW("Warsaw", "Poland"),
        WASHINGTON_DC("Washington, D.C.", "US"),
        DETROIT("Detroit", "US"),
        LOS_ANGELES("Los Angeles", "US"),
        MOSCOW("Moscow", "Russia");

        String name, country;

        Location(final String name, final String country){
            this.name = name;
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public String getWeather(){
            return getOpenWeatherMapClient().currentWeather().single().byCityName(name).language(Language.ENGLISH).retrieve().asJava().getWeatherState().getName();
        }
    }

    public class InventoryClickListener implements Listener {

        @EventHandler
        public void handle(final InventoryClickEvent event){
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getItemMeta() == null) return;

            final Player player = (Player) event.getWhoClicked();
            final ServerPlayer serverPlayer = Base.getServerPlayerHandler().getPlayer(player.getUniqueId());

            if(event.getView().getTitle().equals("§8§l» §e§lRealistic weather")){
                event.setCancelled(true);

                if(Objects.requireNonNull(configuration.getString("key")).equalsIgnoreCase("NULL")){
                    player.sendMessage(Base.PREFIX + "§c§oSorry! To be able to use this module, you have to create an account at \"https://openweathermap.org/\" and enter the API code in \"plugins/ServerManager/realistic_weather.yml > key\". ");
                    return;
                }

                switch (event.getCurrentItem().getItemMeta().getDisplayName()){
                    case "§8§l» §aEnable Module":
                        configuration.set("realistic_weather_use", true);
                        saveConfig();

                        openInventory(player);
                        player.sendMessage(Base.PREFIX + "§aSuccessful! The weather was adapted to §o" + configuration.getString("location"));
                        break;
                    case "§8§l» §cDisable Module":
                        configuration.set("realistic_weather_use", false);
                        saveConfig();

                        openInventory(player);
                        break;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§7§8| §2")){
                    final String city = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§7§8| §2", "");
                    Location newLocation = null;

                    for (Location location : Location.values()) {
                        if(city.equals(location.getName()))
                            newLocation = location;
                    }

                    if(newLocation != null){
                        configuration.set("location", newLocation.getName());
                        saveConfig();
                        openInventory(player);
                    } else
                        player.sendMessage(Base.PREFIX + "§c§oSorry! §c§oAn unknown error has occurred");
                }
            }
        }
    }
}
