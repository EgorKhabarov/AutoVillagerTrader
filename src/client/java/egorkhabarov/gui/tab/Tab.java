package egorkhabarov.gui.tab;

import egorkhabarov.gui.AbstractTabScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Tab implements Drawable {
    public static final Identifier TAB_ABOVE_LEFT = Identifier.of("auto_villager_trader", "tab/tab_above_left");
    public static final Identifier TAB_ABOVE_MIDDLE = Identifier.of("auto_villager_trader", "tab/tab_above_middle");
    public static final Identifier TAB_ABOVE_RIGHT = Identifier.of("auto_villager_trader", "tab/tab_above_right");
    public static final Identifier TAB_ABOVE_LEFT_HOVERED = Identifier.of("auto_villager_trader", "tab/tab_above_left_hovered");
    public static final Identifier TAB_ABOVE_MIDDLE_HOVERED = Identifier.of("auto_villager_trader", "tab/tab_above_middle_hovered");
    public static final Identifier TAB_ABOVE_RIGHT_HOVERED = Identifier.of("auto_villager_trader", "tab/tab_above_right_hovered");
    public static final Identifier TAB_ABOVE_LEFT_SELECTED = Identifier.of("auto_villager_trader", "tab/tab_above_left_selected");
    public static final Identifier TAB_ABOVE_MIDDLE_SELECTED = Identifier.of("auto_villager_trader", "tab/tab_above_middle_selected");
    public static final Identifier TAB_ABOVE_RIGHT_SELECTED = Identifier.of("auto_villager_trader", "tab/tab_above_right_selected");
    private static final int TAB_WIDTH = 28;
    private static final int TAB_HEIGHT = 28;
    private final int index;
    private final TabType tabType;
    private final int x;
    private final int y;
    private final AbstractTabScreen target;
    private final ItemStack item;
    private boolean selected;

    public Tab(
        int index,
        TabType tabType,
        int x,
        int y,
        AbstractTabScreen target,
        Item item
    ) {
        this.index = index;
        this.tabType = tabType;
        this.x = x;
        this.y = y;
        this.target = target;
        this.item = item.getDefaultStack();
    }

    private Identifier getTabAboveIdentifier(boolean hover) {
        if (this.tabType == TabType.LEFT) {
            if (selected) {
                return Tab.TAB_ABOVE_LEFT_SELECTED;
            }
            return hover ? Tab.TAB_ABOVE_LEFT_HOVERED : Tab.TAB_ABOVE_LEFT;
        }
        else if (this.tabType == TabType.MIDDLE) {
            if (selected) {
                return Tab.TAB_ABOVE_MIDDLE_SELECTED;
            }
            return hover ? Tab.TAB_ABOVE_MIDDLE_HOVERED : Tab.TAB_ABOVE_MIDDLE;
        }
        else if (this.tabType == TabType.RIGHT) {
            if (selected) {
                return Tab.TAB_ABOVE_RIGHT_SELECTED;
            }
            return hover ? Tab.TAB_ABOVE_RIGHT_HOVERED : Tab.TAB_ABOVE_RIGHT;
        }
        return Tab.TAB_ABOVE_MIDDLE;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = this.x + (TAB_WIDTH * this.index);
        int y = this.y - TAB_HEIGHT;
        boolean isHovered = mouseX >= x && mouseX < x + TAB_WIDTH && mouseY >= y && mouseY < y + TAB_HEIGHT;

        context.drawGuiTexture(
            RenderPipelines.GUI_TEXTURED,
            this.getTabAboveIdentifier(isHovered),
            x,
            y,
            28,
            32
        );
        context.drawItem(this.item, x+6, y+9);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.selected) {
                return false;
            }
            int dx = 28;
            int dy = 28;
            int x = this.x + (dx * this.index);
            int y = this.y - dy;

            return mouseX >= x && mouseX < x + TAB_WIDTH && mouseY >= y && mouseY < y + TAB_HEIGHT;
        }
        return false;
    }

    public AbstractTabScreen getTarget() {
        return target;
    }

    public int getIndex() {
        return index;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
