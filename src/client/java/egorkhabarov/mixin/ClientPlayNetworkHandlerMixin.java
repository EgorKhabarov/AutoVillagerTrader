package egorkhabarov.mixin;

import egorkhabarov.AutoVillagerTraderModClient;
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
        if (client.player == null) {
            return;
        }
        TradeOfferList offers = packet.getOffers();
        System.out.println("    onSetTradeOffers ++");
        VillagerTradeExecutor.finishAutoTrade(offers);
        System.out.println("client.player.closeHandledScreen();");
        client.player.closeHandledScreen();
    }
}
