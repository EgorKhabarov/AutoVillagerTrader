package egorkhabarov.mixin;

import egorkhabarov.AutoVillagerTraderModClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(@Nullable Screen screen, CallbackInfo ci) {
        if (this.player == null) {
            return;
        }
        if (screen instanceof MerchantScreen && AutoVillagerTraderModClient.CONFIG.enabled) {
            ci.cancel();
        }
    }
}
