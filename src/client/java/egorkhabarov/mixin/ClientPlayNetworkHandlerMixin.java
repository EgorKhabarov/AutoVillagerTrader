package egorkhabarov.mixin;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.logic.VillagerTradeExecutor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onSetTradeOffers", at = @At("HEAD"))
    private void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        if (VillagerCache.currentVillager == null || !AutoVillagerTraderModClient.CONFIG.enabled) {
            return;
        }
        int syncId = packet.getSyncId();
        TradeOfferList offers = packet.getOffers();
        VillagerTradeExecutor.finishAutoTrade(syncId, offers);
    }
}
