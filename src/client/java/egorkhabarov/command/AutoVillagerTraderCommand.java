package egorkhabarov.command;

import egorkhabarov.AutoVillagerTraderModClient;
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
                    int v = 1;
                    client.player.sendMessage(Text.literal("Reloaded "+v).formatted(Formatting.GREEN), false);
                    client.player.sendMessage(Text.literal("Reloaded "+v).formatted(Formatting.GREEN), true);
                    return 1;
                })
            );
        });
    }
}
