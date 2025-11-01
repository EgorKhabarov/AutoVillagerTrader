package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.config.ConfigData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler {
    private static long lastScanTime = 0;
    private static long lastInventoryCheckTime = 0;

    private static boolean inventoryCheckLastResult = true;
    private static final long INVENTORY_CHECK_INTERVAL_MS  = 3000;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            ConfigData config = AutoVillagerTraderModClient.CONFIG;
            if (
                client.player == null
                    || client.world == null
                    || config == null
                    || !config.enabled
                    || !config.auto_finder_enabled
                    || VillagerCache.currentVillager != null
            ) {
                return;
            }

            long now = System.currentTimeMillis();
            long interval = config.scan_interval_ms;

            if (now - lastInventoryCheckTime >= Math.max(interval, INVENTORY_CHECK_INTERVAL_MS)) {
                lastInventoryCheckTime = now;
                inventoryCheckLastResult = InventoryChecker.canTrade();
            }

            if (!inventoryCheckLastResult) {
                return;
            }

            if (now - lastScanTime >= interval) {
                lastScanTime = now;
                VillagerFinder.tick();
            }
        });
    }
}
