package egorkhabarov.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import egorkhabarov.AutoVillagerTraderModClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    private static final String CONFIG_PATH = "config/" + AutoVillagerTraderModClient.MOD_ID + ".json";

    private static ConfigData configInstance;

    public static ConfigData getConfig() {
        if (ConfigManager.configInstance == null) {
            ConfigManager.configInstance = ConfigManager.loadOrCreateConfig();
        }
        return ConfigManager.configInstance;
    }

    public static ConfigData reload() {
        ConfigManager.configInstance = ConfigManager.loadOrCreateConfig();
        return ConfigManager.configInstance;
    }

    private static ConfigData loadOrCreateConfig() {
        File file = new File(ConfigManager.CONFIG_PATH);

        if (!file.exists()) {
            System.out.println("[VillagerTradeMod] Config not found, creating default...");
            try {
                if (!file.getParentFile().mkdirs()) {
                    System.err.println("[VillagerTradeMod] Failed to create config directory");
                }
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    writer.write(ConfigManager.getDefaultConfigJson());
                }
                System.out.println("[VillagerTradeMod] Default config created at " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("[VillagerTradeMod] Failed to create config file: " + e.getMessage());
            }
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            ConfigData cfg = GSON.fromJson(reader, ConfigData.class);
            if (cfg == null) {
                throw new JsonSyntaxException("Empty or invalid config");
            }
            return cfg;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[VillagerTradeMod] Failed to load config: " + e.getMessage());
            System.err.println("[VillagerTradeMod] Falling back to default config...");
            return ConfigManager.getDefaultConfig();
        }
    }

    private static String getDefaultConfigJson() {
        return """
{
    "enabled": false,
    "auto_finder_enabled": false,
    "scan_interval_ms": 1000,
    "scan_radius": 0.5,
    "villager_cache_ttl": 10000,
    "need_see": true,
    "professions": ["minecraft:farmer"],
    "trades": [
        {
            "enabled": true,
            "left": {
                "item": "minecraft:melon",
                "count": {
                    "condition": "<",
                    "value": 5
                }
            },
            "right": {
                "item": "minecraft:emerald",
                "count": {
                    "condition": "=",
                    "value": 1
                }
            }
        },
        {
            "enabled": true,
            "left": {
                "item": "minecraft:pumpkin",
                "count": {
                    "condition": "<",
                    "value": 5
                }
            },
            "right": {
                "item": "minecraft:emerald",
                "count": {
                    "condition": "=",
                    "value": 1
                }
            }
        }
    ]
}
        """;
    }

    private static ConfigData getDefaultConfig() {
        return GSON.fromJson(ConfigManager.getDefaultConfigJson(), ConfigData.class);
    }

    public static void saveConfig() {
        File file = new File(ConfigManager.CONFIG_PATH);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(ConfigManager.configInstance, writer);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[VillagerTradeMod] Failed to save config: " + e.getMessage());
        }
    }
}
