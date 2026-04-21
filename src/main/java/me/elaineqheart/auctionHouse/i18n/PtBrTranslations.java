package me.elaineqheart.auctionHouse.i18n;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Bundled pt_BR translations so the search index works in Portuguese without the Locale-API plugin.
 * The raw translation keys come from Mojang's official pt_br.json, filtered to only
 * item/block/enchantment/effect entries.
 */
public final class PtBrTranslations {

    private static final Map<Material, String> MATERIALS = new HashMap<>();
    private static final Map<String, String> ENCHANTMENTS = new HashMap<>();
    private static final Map<String, String> RAW = new HashMap<>();

    private PtBrTranslations() {}

    public static void load(Plugin plugin) {
        try (InputStream in = plugin.getResource("pt_br.json")) {
            if (in == null) {
                plugin.getLogger().warning("pt_br.json not found in plugin resources — Portuguese search disabled.");
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                RAW.put(entry.getKey(), entry.getValue().getAsString());
            }
            for (Material m : Material.values()) {
                String key = m.name().toLowerCase();
                String translated = RAW.get("item.minecraft." + key);
                if (translated == null) translated = RAW.get("block.minecraft." + key);
                if (translated != null) MATERIALS.put(m, translated);
            }
            for (Enchantment e : Enchantment.values()) {
                NamespacedKey key = e.getKey();
                String translated = RAW.get("enchantment." + key.getNamespace() + "." + key.getKey());
                if (translated != null) ENCHANTMENTS.put(key.toString(), translated);
            }
            plugin.getLogger().info("Loaded " + MATERIALS.size() + " material and " + ENCHANTMENTS.size() + " enchantment pt_BR translations.");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load pt_br.json: " + e.getMessage());
        }
    }

    public static String getMaterial(Material material) {
        return MATERIALS.getOrDefault(material, "");
    }

    public static String getEnchantment(Enchantment enchantment) {
        return ENCHANTMENTS.getOrDefault(enchantment.getKey().toString(), "");
    }

    public static String getPotion(ItemStack stack, PotionType type) {
        if (type == null) return "";
        String prefix = switch (stack.getType()) {
            case SPLASH_POTION -> "item.minecraft.splash_potion.effect.";
            case LINGERING_POTION -> "item.minecraft.lingering_potion.effect.";
            case TIPPED_ARROW -> "item.minecraft.tipped_arrow.effect.";
            default -> "item.minecraft.potion.effect.";
        };
        String effect = type.getKey().getKey();
        return RAW.getOrDefault(prefix + effect, "");
    }
}
