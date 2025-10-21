package egorkhabarov;

import egorkhabarov.cache.VillagerCache;
import egorkhabarov.logic.VillagerFinder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler {
    private static long lastScanTime = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            if (
                client.player == null
                    || client.world == null
                    || AutoVillagerTraderModClient.CONFIG == null
                    || VillagerCache.currentVillager != null
                    || !AutoVillagerTraderModClient.CONFIG.enabled
            ) {
                return;
            }

            long now = System.currentTimeMillis();
            long interval = AutoVillagerTraderModClient.CONFIG.scan_interval_ms;

            if (now - lastScanTime >= interval) {
                lastScanTime = now;
                VillagerFinder.tick();
            }
        });
    }
}
