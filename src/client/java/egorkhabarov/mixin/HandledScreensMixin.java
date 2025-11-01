package egorkhabarov.mixin;

import egorkhabarov.AutoVillagerTraderModClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(HandledScreens.class)
public class HandledScreensMixin {
    @Inject(method = "open", at = @At("HEAD"), cancellable = true)
    private static <T extends ScreenHandler> void openMerchantScreen(ScreenHandlerType<T> type, MinecraftClient client, int id, Text component, CallbackInfo ci) {
        if (type == ScreenHandlerType.MERCHANT && AutoVillagerTraderModClient.CONFIG.enabled) {
            ci.cancel();
        }
    }
}
