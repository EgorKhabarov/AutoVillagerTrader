package egorkhabarov.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding debugKey;

    public static void register() {
        debugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autotrader.debug",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.autotrader"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (debugKey.wasPressed()) {
                if (client.player == null) {
                    continue;
                }
                System.out.println("DEBUG KEY: currentScreenHandler: "+client.player.currentScreenHandler);
            }
        });
    }
}
