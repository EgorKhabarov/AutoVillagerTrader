package egorkhabarov.gui.slot;

import egorkhabarov.config.Condition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Slot {
    private final int x;
    private final int y;
    private final int width = 18;
    private final int height = 18;
    private @Nullable ItemStack stack;
    private @Nullable Condition condition;

    public Slot(int x, int y, @Nullable ItemStack stack) {
        this.x = x;
        this.y = y;
        this.stack = stack;
    }

    public void setStack(@Nullable ItemStack stack) {
        this.stack = stack;
    }

    public void setCondition(@Nullable Condition condition) {
        this.condition = condition;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        if (this.stack == null || this.stack.isEmpty()) {
            return;
        }
        Slot.renderSlotItem(context, textRenderer, this.stack, this.condition, this.x, this.y, this.width, this.height, mouseX, mouseY);
    }

    private static void renderItem(
        DrawContext context,
        TextRenderer textRenderer,
        @NotNull ItemStack itemStack,
        @Nullable Condition condition,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        boolean isHoverable,
        boolean fillOverlay
    ) {
        context.drawItem(itemStack, x, y);
        if (condition != null) {
            String overlay = getOverlay(condition);
            context.drawStackOverlay(textRenderer, itemStack, x, y, overlay);
        } else {
            context.drawStackOverlay(textRenderer, itemStack, x, y);
        }
        boolean isHovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        if (isHoverable && isHovered) {
            if (fillOverlay) {
                context.fill(x, y, x + width, y + height, 0x80FFFFFF);
            }
            context.drawItemTooltip(textRenderer, itemStack, mouseX, mouseY);
        }
    }

    private static @NotNull String getOverlay(@NotNull Condition condition) {
        String overlay = (
            condition.condition.equals("=") || condition.condition.equals("==")
            ? "" : condition.condition
        ) + (
            condition.value == 1 ? "" : condition.value
        );
        if (condition.condition.equals(">=") && condition.value == 1) {
            overlay = "*";
        }
        if (condition.condition.equals(">") && condition.value == 0) {
            overlay = "*";
        }
        if (condition.condition.equals(">") && condition.value == 1) {
            overlay = ">1";
        }
        return overlay;
    }

    public static void renderSlotItem(
        DrawContext context,
        TextRenderer textRenderer,
        @NotNull ItemStack itemStack,
        @Nullable Condition condition,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY
    ) {
        Slot.renderItem(
            context,
            textRenderer,
            itemStack,
            condition,
            x,
            y,
            width,
            height,
            mouseX,
            mouseY,
            true,
            true
        );
    }

    public static void renderItem(
        DrawContext context,
        TextRenderer textRenderer,
        @NotNull ItemStack itemStack,
        @Nullable Condition condition,
        int x,
        int y
    ) {
        Slot.renderItem(
            context,
            textRenderer,
            itemStack,
            condition,
            x,
            y,
            0,
            0,
            0,
            0,
            false,
            false
        );
    }
}
