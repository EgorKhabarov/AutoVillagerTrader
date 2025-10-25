package egorkhabarov.keybinds;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.gui.VillagerTradeEditorScreen;
import egorkhabarov.util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    private static KeyBinding openMenuKey;
    private static KeyBinding toggleAutoTraderKey;
    private static KeyBinding toggleAutoFinderKey;

    private static boolean wasOpenMenuKeyPressed = false;
    private static boolean wasToggleAutoTraderKeyPressed = false;
    private static boolean wasToggleAutoFinderKeyPressed = false;

    public static void register() {
        Keybinds.openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autotrader.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.autotrader"
        ));
        Keybinds.toggleAutoTraderKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autotrader.toggle_auto_trader",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.autotrader"
        ));
        Keybinds.toggleAutoFinderKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autotrader.toggle_auto_finder",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.autotrader"
        ));

        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            if (client.player == null) {
                return;
            }

            boolean openMenuKeyNow = Keybinds.openMenuKey.isPressed();
            if (!openMenuKeyNow && Keybinds.wasOpenMenuKeyPressed) {
                client.setScreen(new VillagerTradeEditorScreen());
            }
            Keybinds.wasOpenMenuKeyPressed = openMenuKeyNow;

            boolean toggleAutoTraderKeyNow = Keybinds.toggleAutoTraderKey.isPressed();
            if (!toggleAutoTraderKeyNow && Keybinds.wasToggleAutoTraderKeyPressed) {
                AutoVillagerTraderModClient.CONFIG.enabled = !AutoVillagerTraderModClient.CONFIG.enabled;
                ConfigManager.saveConfig();
                ChatUtils.sendAutoTraderStatusMessage();
            }
            Keybinds.wasToggleAutoTraderKeyPressed = toggleAutoTraderKeyNow;

            boolean toggleAutoFinderKeyNow = Keybinds.toggleAutoFinderKey.isPressed();
            if (!toggleAutoFinderKeyNow && Keybinds.wasToggleAutoFinderKeyPressed) {
                AutoVillagerTraderModClient.CONFIG.auto_finder_enabled = !AutoVillagerTraderModClient.CONFIG.auto_finder_enabled;
                ConfigManager.saveConfig();
                ChatUtils.sendAutoFinderStatusMessage();
            }
            Keybinds.wasToggleAutoFinderKeyPressed = toggleAutoFinderKeyNow;
        });
    }
}
