package me.elaineqheart.auctionHouse.data.persistentStorage.local.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.OldLayout;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {

    public static M messages = new M();
    public static Config displays = new Config();
    public static BannedPlayers bannedPlayers = new BannedPlayers();
    public static Permissions permissions = new Permissions();
    public static Blacklist blacklist = new Blacklist();
    public static Config categories = new Config();
    public static PlayerPreferences playerPreferences = new PlayerPreferences();
    public static Layout layout = new Layout();
    public static TransactionLogger transactionLogger = new TransactionLogger();
    private static final List<Config> list = new ArrayList<>();

    public static void setupConfigs() {
        //Setup config.yml
        AuctionHouse.getPlugin().reloadConfig();
        AuctionHouse.getPlugin().getConfig().options().copyDefaults(true);
        AuctionHouse.getPlugin().saveConfig();
        messages.setup("messages.yml", true, "");
        displays.setup("displays.yml", false, "/data");
        displaysBackwardsCompatibility();
        bannedPlayers.setup("bannedPlayers.yml", false, "/data");
        permissions.setup("permissions.yml", true, "");
        blacklist.setup("blacklist.yml", false, "/data");
        categories.setup("categories.yml", false, "/data");
        playerPreferences.setup("playerPreferences.yml", false, "/data");
        if(!oldVersion21()) { //compatibility to version 1.21.4
            layout.setup("layout.yml", true, "");
        } else {
            layout.setup("layout.yml", false, "");
            if (!layout.getCustomFile().getBoolean("old-layout")) OldLayout.saveOldLayout();
        }
        transactionLogger.setup(transactionLogger.getName(), false, "/logs");
        permissionsSetup();
    }

    public static boolean backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        return !Objects.equals(c.getString("plugin-version"), AuctionHouse.getPlugin().getDescription().getVersion());
    }

    public static void reloadConfigs() {
        AuctionHouse.getPlugin().reloadConfig();
        getList().forEach(Config::reload);
    }

    private static List<Config> getList() {
        if(list.isEmpty()) list.addAll(List.of(messages, displays, bannedPlayers, permissions, blacklist, categories, playerPreferences, layout, transactionLogger));
        return list;
    }

    private static void permissionsSetup() {
        if(permissions.getCustomFile().getConfigurationSection("auction-slots") == null) {
            permissions.getCustomFile().createSection("auction-slots");
            permissions.save();
        }
        if(permissions.getCustomFile().getConfigurationSection("bin-auction-duration") == null) {
            permissions.getCustomFile().createSection("bin-auction-duration");
            permissions.save();
        }
        if(permissions.getCustomFile().getConfigurationSection("bid-auction-duration") == null) {
            permissions.getCustomFile().createSection("bid-auction-duration");
            permissions.save();
        }
    }

    private static void displaysBackwardsCompatibility() {
        Set<Integer> oldSet = null;
        FileConfiguration customFile = displays.getCustomFile();
        try {
            // This method is for backwards compatibility
            oldSet = customFile.getKeys(false).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException ignored) {}

        //This section of code is needed, even without backwards compatibility
        if (customFile.getConfigurationSection("displays") == null) {
            customFile.createSection("displays");
        }

        if(oldSet != null) {
            for (Integer displayID : oldSet) {
                Objects.requireNonNull(customFile.getConfigurationSection("displays")).set(String.valueOf(displayID), customFile.get(String.valueOf(displayID)));
                customFile.set(String.valueOf(displayID), null); // Remove the old key
            }
        }
        displays.save();
    }

    public static boolean oldVersion21() {
        String version = Bukkit.getServer().getVersion();
        List<String> oldVersions = List.of("1.21.4", "1.21.3", "1.21.2", "1.21.1");
        for(String oldVersion : oldVersions) {
            if(version.contains(oldVersion)) return true;
        }
        return false;
    }

}
