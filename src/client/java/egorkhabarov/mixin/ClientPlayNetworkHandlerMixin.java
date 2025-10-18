package egorkhabarov.mixin;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.cache.VillagerTradeQueue;
import egorkhabarov.logic.VillagerTradeExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onSetTradeOffers", at = @At("TAIL"))
    private void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!AutoVillagerTraderModClient.CONFIG.enabled) {
            return;
        }
        if (
            client.player == null
            // || VillagerCache.currentVillager == null
            // || !VillagerTradeQueue.busy
            // || VillagerCache.skipOffer
        ) {
            // if (VillagerCache.skipOffer) {
            //     ci.cancel();
            //     VillagerCache.skipOffer = false;
            //     System.out.println("SKIP TradeOffers because skipOffer=true");
            //     return;
            // }
            // ci.cancel();
            // if (client.player == null) {
            //     return;
            // }
            // client.execute(client.player::closeHandledScreen);
            // System.out.println("CLOSE");
            return;
        }
        // System.out.println("onSetTradeOffers player=" + client.player
        //     + " currentVillager=" + VillagerCache.currentVillager
        //     + " enabled=" + AutoVillagerTraderModClient.CONFIG.enabled
        //     // + " skipOffer=" + VillagerCache.skipOffer
        //     + " busy=" + VillagerTradeQueue.busy
        //     + " PENDING.size()=" + VillagerTradeQueue.PENDING.size()
        // );
        TradeOfferList offers = packet.getOffers();
        // client.execute(() -> {
            // ci.cancel();
            System.out.println("    onSetTradeOffers ++");
            VillagerTradeExecutor.finishAutoTrade(offers);
            System.out.println("client.player.closeHandledScreen();");
            client.player.closeHandledScreen();
            // System.out.println("VillagerTradeQueue.markDone();");
            // VillagerTradeQueue.markDone();
            // System.out.println("AFTER markDone");
        // });
    }
}
