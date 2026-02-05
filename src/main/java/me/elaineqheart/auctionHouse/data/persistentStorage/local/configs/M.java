package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class M extends Config {

    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    // AuctionHouse.getPlugin().saveResource("messages.yml", false);

    public static FileConfiguration get() {
        return ConfigManager.messages.getCustomFile();
    }

    private static String getValue(String key, boolean convertNewLine) {
        String message = get().getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return convertNewLine ? message.replace("&n", "\n") : message;
    }

    //this is to replace placeholders like %player%
    public static String getFormatted(String key, String... replacements) {
        String message = getValue(key,true);
        message = replacePlaceholders(key, message, replacements);
        return adventureApi(message);
    }
    public static String getFormatted(String key, double price, String... replacements) {
        String message = getValue(key,true);
        message = replacePlaceholders(key, message, replacements);
        message = message.replace("%price%", StringUtils.formatPrice(price));
        message = message.replace("%number%", StringUtils.formatNumber(price));
        return adventureApi(message);
    }

    public static List<String> getLoreList(String key, String... replacements) {
        String message = getValue(key,false);
        message = replacePlaceholders(key, message, replacements);
        List<String> list = Arrays.asList(message.split("&n"));
        list.replaceAll(M::adventureApi);
        return list;
    }
    public static List<String> getLoreList(String key, double price, String... replacements) {
        String message = getValue(key,false);
        message = replacePlaceholders(key, message, replacements);
        message = message.replace("%price%", StringUtils.formatPrice(price));
        message = message.replace("%number%", StringUtils.formatNumber(price));
        List<String> list = Arrays.asList(message.split("&n"));
        list.replaceAll(M::adventureApi);
        return list;
    }

    private static String replacePlaceholders(String key, String message, String... replacements) {
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    private static String adventureApi(String input) {
        Component comp;
        try {
            comp = mm.deserialize(input);
        } catch (Exception e) {
            comp = legacy.deserialize(input);
        }
        return legacy.serialize(comp);
    }

}
