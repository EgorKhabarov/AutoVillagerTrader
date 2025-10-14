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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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
        File file = new File(CONFIG_PATH);

        if (!file.exists()) {
            System.out.println("[VillagerTradeMod] Config not found, creating default...");
            try {
                file.getParentFile().mkdirs();
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
    "enabled": true,
    "cooldown_ms": 5000,
    "professions": {
        "minecraft:farmer": [
            {
                "enabled": true,
                "cooldown_ms": 500,
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
                "cooldown_ms": 500,
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
}
        """;
    }

    private static ConfigData getDefaultConfig() {
        ConfigData cfg = new ConfigData();
        cfg.enabled = true;
        cfg.comment = "Автоторговля с фермерами для продажи урожая";
        cfg.scan_interval_ms = 1000;

        TradeItemSide leftMelon = new TradeItemSide();
        leftMelon.item = "minecraft:melon";
        leftMelon.count = new Condition();
        leftMelon.count.condition = "<";
        leftMelon.count.value = 5;

        TradeItemSide rightEmerald = new TradeItemSide();
        rightEmerald.item = "minecraft:emerald";
        rightEmerald.count = new Condition();
        rightEmerald.count.condition = "=";
        rightEmerald.count.value = 1;

        TradeRule melonTrade = new TradeRule();
        melonTrade.enabled = true;
        melonTrade.comment = "Продажа арбузов фермеру";
        melonTrade.cooldown_ms = 1000;
        melonTrade.left = leftMelon;
        melonTrade.right = rightEmerald;

        TradeItemSide leftPumpkin = new TradeItemSide();
        leftPumpkin.item = "minecraft:pumpkin";
        leftPumpkin.count = new Condition();
        leftPumpkin.count.condition = "<";
        leftPumpkin.count.value = 5;

        TradeRule pumpkinTrade = new TradeRule();
        pumpkinTrade.enabled = true;
        pumpkinTrade.comment = "Продажа тыкв фермеру";
        pumpkinTrade.cooldown_ms = 1000;
        pumpkinTrade.left = leftPumpkin;
        pumpkinTrade.right = rightEmerald;

        cfg.professions = Map.of("minecraft:farmer", List.of(melonTrade, pumpkinTrade));
        return cfg;
    }
}
