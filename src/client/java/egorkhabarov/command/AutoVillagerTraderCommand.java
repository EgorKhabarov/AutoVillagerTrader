package egorkhabarov.command;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.cache.VillagerTradeQueue;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

public class AutoVillagerTraderCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("avt_reload")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    AutoVillagerTraderModClient.CONFIG = ConfigManager.reload();
                    ChatUtils.sendReloadMessage();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("avt_reset")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    VillagerCache.currentVillager = null;
                    VillagerCache.clear();
                    VillagerTradeQueue.PENDING.clear();
                    VillagerTradeQueue.busy = false;
                    // VillagerCache.skipOffer = false;
                    ChatUtils.sendResetMessage();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("avt_enable")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    AutoVillagerTraderModClient.CONFIG.enabled = true;
                    ConfigManager.saveConfig();
                    ChatUtils.sendAutoTraderStatusMessage();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("avt_disable")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    AutoVillagerTraderModClient.CONFIG.enabled = false;
                    ConfigManager.saveConfig();
                    ChatUtils.sendAutoTraderStatusMessage();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("avt_auto_finder_enable")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    AutoVillagerTraderModClient.CONFIG.auto_finder_enabled = true;
                    ConfigManager.saveConfig();
                    ChatUtils.sendAutoFinderStatusMessage();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("avt_auto_finder_disable")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    AutoVillagerTraderModClient.CONFIG.auto_finder_enabled = false;
                    ConfigManager.saveConfig();
                    ChatUtils.sendAutoFinderStatusMessage();
                    return 1;
                })
            );
        });
    }
}
