package egorkhabarov.mixin;

import egorkhabarov.cache.VillagerCache;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.logic.VillagerFinder;

@Mixin(MinecraftClient.class)
public class ClientTickMixin {
    @Unique
    private long lastScanTime = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onClientTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
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
            this.lastScanTime = now;
            VillagerFinder.tick();
        }
    }
}
