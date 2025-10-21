package egorkhabarov.keybinds;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    private static KeyBinding toggleKey;

    public static void register() {
        Keybinds.toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autotrader.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.autotrader"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Keybinds.toggleKey.wasPressed()) {
                if (client.player == null) {
                    continue;
                }
                AutoVillagerTraderModClient.CONFIG.enabled = !AutoVillagerTraderModClient.CONFIG.enabled;
                ConfigManager.saveConfig();
                ChatUtils.sendStatusMessage();
            }
        });
    }
}
