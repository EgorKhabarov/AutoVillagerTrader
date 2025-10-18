package egorkhabarov.command;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.cache.VillagerTradeQueue;
import egorkhabarov.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                    client.player.sendMessage(
                        Text.literal("AVT config successfully reloaded")
                            .formatted(Formatting.GREEN),
                        false
                    );
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
                    client.player.sendMessage(
                        Text.literal("Successfully reset")
                            .formatted(Formatting.GREEN),
                        false
                    );
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
                    // TODO AutoVillagerTraderModClient.CONFIG.save();
                    client.player.sendMessage(
                        Text.literal("Successfully enabled")
                            .formatted(Formatting.GREEN),
                        false
                    );
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
                    // TODO AutoVillagerTraderModClient.CONFIG.save();
                    client.player.sendMessage(
                        Text.literal("Successfully disabled")
                            .formatted(Formatting.GREEN),
                        false
                    );
                    return 1;
                })
            );
        });
    }
}
