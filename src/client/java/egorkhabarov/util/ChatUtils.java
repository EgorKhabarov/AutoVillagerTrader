package egorkhabarov.util;

import egorkhabarov.AutoVillagerTraderModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class ChatUtils {
    private static final Text prefix = Text.literal("[")
        .append(Text.literal("AVT").formatted(Formatting.YELLOW))
        .append(Text.literal("] "));

    public static void sendModMessage(Object... parts) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        MutableText result = ChatUtils.prefix.copy();

        for (Object part : parts) {
            if (part instanceof Text text) {
                result.append(text);
            } else if (part instanceof String str) {
                result.append(Text.literal(str));
            } else {
                result.append(Text.literal(String.valueOf(part)));
            }
        }
        client.player.sendMessage(result, false);
    }

    public static void sendErrorMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.RED));
    }

    public static void sendConfirmationMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.GREEN));
    }

    public static void sendAutoTraderStatusMessage() {
        MutableText message = Text.translatable(
            AutoVillagerTraderModClient.CONFIG.enabled
            ? "message.autotrader.auto_trader_enabled"
            : "message.autotrader.auto_trader_disabled"
        );
        ChatUtils.sendModMessage(message.formatted(Formatting.GREEN));
    }

    public static void sendAutoFinderStatusMessage() {
        MutableText message = Text.translatable(
            AutoVillagerTraderModClient.CONFIG.auto_finder_enabled
            ? "message.autotrader.auto_finder_enabled"
            : "message.autotrader.auto_finder_disabled"
        );
        ChatUtils.sendModMessage(message.formatted(Formatting.GREEN));
    }

    public static void sendReloadMessage() {
        MutableText message = Text.translatable("message.autotrader.reload");
        ChatUtils.sendModMessage(message.formatted(Formatting.GREEN));
    }

    public static void sendResetMessage() {
        MutableText message = Text.translatable("message.autotrader.reset");
        ChatUtils.sendModMessage(message.formatted(Formatting.GREEN));
    }
}
