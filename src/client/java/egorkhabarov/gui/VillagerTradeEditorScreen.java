package egorkhabarov.gui;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.Condition;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.config.TradeItemSide;
import egorkhabarov.config.TradeRule;
import egorkhabarov.gui.slot.Slot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class VillagerTradeEditorScreen extends Screen {
    private static final Identifier TEXT_FIELD_LONG_TEXTURE        = Identifier.of("auto_villager_trader", "container/text_field_long");
    private static final Identifier TEXT_FIELD_SHORT_TEXTURE       = Identifier.of("auto_villager_trader", "container/text_field_short");
    private static final Identifier TEXT_FIELD_LONG_ERROR_TEXTURE  = Identifier.of("auto_villager_trader", "container/text_field_long_error");
    private static final Identifier TEXT_FIELD_SHORT_ERROR_TEXTURE = Identifier.of("auto_villager_trader", "container/text_field_short_error");

    private static final Identifier TEXTURE                        = Identifier.of("auto_villager_trader", "textures/gui/villager_trade_add.png");

    static final Identifier BUTTON_TEXTURE = Identifier.ofVanilla("container/beacon/button");
    static final Identifier BUTTON_DISABLED_TEXTURE = Identifier.ofVanilla("container/beacon/button_disabled");
    static final Identifier BUTTON_SELECTED_TEXTURE = Identifier.ofVanilla("container/beacon/button_selected");
    static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/beacon/button_highlighted");
    static final Identifier CONFIRM_TEXTURE = Identifier.ofVanilla("container/beacon/confirm");
    static final Identifier CANCEL_TEXTURE = Identifier.ofVanilla("container/beacon/cancel");

    private final Screen parent;
    private static final int WIDTH = 276;
    private static final int HEIGHT = 166;

    private VillagerTradeEditorScreen.ConfirmButtonWidget confirmButton;
    private VillagerTradeEditorScreen.CancelButtonWidget cancelButton;

    private ColoredTextFieldWidget leftItemField;
    private ColoredTextFieldWidget leftConditionField;
    private ColoredTextFieldWidget leftConditionValueField;
    private ColoredTextFieldWidget left2ItemField;
    private ColoredTextFieldWidget left2ConditionField;
    private ColoredTextFieldWidget left2ConditionValueField;
    private ColoredTextFieldWidget rightItemField;
    private ColoredTextFieldWidget rightConditionField;
    private ColoredTextFieldWidget rightConditionValueField;

    private boolean leftItemError = true;
    private boolean leftConditionError = true;
    private boolean leftConditionValueError = true;
    private boolean left2ItemError = true;
    private boolean left2ConditionError = true;
    private boolean left2ConditionValueError = true;
    private boolean rightItemError = true;
    private boolean rightConditionError = true;
    private boolean rightConditionValueError = true;

    private final List<Slot> slots = new ArrayList<>();
    private int initialDataIndex;
    private final TradeRule initialData;

    private static final Set<String> conditionValues = Set.of("=", "==", "<", ">", "<=", ">=");

    public VillagerTradeEditorScreen(Screen parent) {
        super(Text.translatable("avt_menu.add_trade.title"));
        this.parent = parent;
        this.initialData = null;
    }

    public VillagerTradeEditorScreen(Screen parent, int index, TradeRule tradeRule) {
        super(Text.translatable("avt_menu.update_trade.title"));
        this.parent = parent;
        this.initialDataIndex = index;
        this.initialData = tradeRule;
    }

    private ColoredTextFieldWidget createTextFieldWidget(int x, int y, int width, int height, Text text, Function<String, Boolean> isValidFunc) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        ColoredTextFieldWidget textField = new ColoredTextFieldWidget(
            this.textRenderer,
            i + x + 3, j + y + 4, width - 7, height - 4,
            text,
            isValidFunc
        );
        textField.setFocusedColor(-2039584);
        textField.setUnfocusedColor(-9408400);

        textField.setDrawsBackground(false);
        textField.setText("");
        return textField;
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        int x1 = 16, x2 = 144, x3 = 184;
        int y1 = 64, y2 = 96, y3 = 128;
        int width1 = 112, width2 = 24, width3 = 24;
        int height1 = 16, height2 = 16, height3 = 16;

        this.cancelButton = new VillagerTradeEditorScreen.CancelButtonWidget(i + 230, j + 78);
        this.addDrawableChild(cancelButton);

        this.confirmButton = new VillagerTradeEditorScreen.ConfirmButtonWidget(i + 230, j + 110);
        this.addDrawableChild(confirmButton);

        this.leftItemField            = createTextFieldWidget(x1, y1, width1, height1, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidItem);
        this.left2ItemField           = createTextFieldWidget(x1, y2, width1, height1, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidItem);
        this.rightItemField           = createTextFieldWidget(x1, y3, width1, height1, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidItem);
        this.leftConditionField       = createTextFieldWidget(x2, y1, width2, height2, Text.translatable("container.repair"), VillagerTradeEditorScreen.conditionValues::contains);
        this.left2ConditionField      = createTextFieldWidget(x2, y2, width2, height2, Text.translatable("container.repair"), VillagerTradeEditorScreen.conditionValues::contains);
        this.rightConditionField      = createTextFieldWidget(x2, y3, width2, height2, Text.translatable("container.repair"), VillagerTradeEditorScreen.conditionValues::contains);
        this.leftConditionValueField  = createTextFieldWidget(x3, y1, width3, height3, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidConditionValue);
        this.left2ConditionValueField = createTextFieldWidget(x3, y2, width3, height3, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidConditionValue);
        this.rightConditionValueField = createTextFieldWidget(x3, y3, width3, height3, Text.translatable("container.repair"), VillagerTradeEditorScreen::isValidConditionValue);

        this.leftItemField.setMaxLength(128);
        this.left2ItemField.setMaxLength(128);
        this.rightItemField.setMaxLength(128);
        this.leftConditionField.setMaxLength(2);
        this.left2ConditionField.setMaxLength(2);
        this.rightConditionField.setMaxLength(2);
        this.leftConditionValueField.setMaxLength(3);
        this.left2ConditionValueField.setMaxLength(3);
        this.rightConditionValueField.setMaxLength(3);

        this.leftItemField.setPlaceholder(Text.of("minecraft id"));
        this.left2ItemField.setPlaceholder(Text.of("minecraft id"));
        this.rightItemField.setPlaceholder(Text.of("minecraft id"));
        this.leftConditionField.setPlaceholder(Text.of("="));
        this.left2ConditionField.setPlaceholder(Text.of("="));
        this.rightConditionField.setPlaceholder(Text.of("="));
        this.leftConditionValueField.setPlaceholder(Text.of("int"));
        this.left2ConditionValueField.setPlaceholder(Text.of("int"));
        this.rightConditionValueField.setPlaceholder(Text.of("int"));

        this.addDrawableChild(this.leftItemField);
        this.addDrawableChild(this.left2ItemField);
        this.addDrawableChild(this.rightItemField);
        this.addDrawableChild(this.leftConditionField);
        this.addDrawableChild(this.left2ConditionField);
        this.addDrawableChild(this.rightConditionField);
        this.addDrawableChild(this.leftConditionValueField);
        this.addDrawableChild(this.left2ConditionValueField);
        this.addDrawableChild(this.rightConditionValueField);

        if (this.leftConditionField.getText().isEmpty()) this.leftConditionField.setText("");
        if (this.left2ConditionField.getText().isEmpty()) this.left2ConditionField.setText("");
        if (this.rightConditionField.getText().isEmpty()) this.rightConditionField.setText("");
        if (this.leftConditionValueField.getText().isEmpty()) this.leftConditionValueField.setText("");
        if (this.left2ConditionValueField.getText().isEmpty()) this.left2ConditionValueField.setText("");
        if (this.rightConditionValueField.getText().isEmpty()) this.rightConditionValueField.setText("");

        this.slots.clear();
        this.slots.add(new Slot(i + 89, j + 22, null));
        this.slots.add(new Slot(i + 115, j + 22, null));
        this.slots.add(new Slot(i + 173, j + 22, null));

        if (this.initialData != null) {
            if (this.initialData.left != null) {
                this.leftItemField.setText(this.initialData.left.item);
                this.leftConditionField.setText(this.initialData.left.count.condition);
                this.leftConditionValueField.setText(this.initialData.left.count.value.toString());
            }
            if (this.initialData.left2 != null) {
                this.left2ItemField.setText(this.initialData.left2.item);
                this.left2ConditionField.setText(this.initialData.left2.count.condition);
                this.left2ConditionValueField.setText(this.initialData.left2.count.value.toString());
            }
            if (this.initialData.right != null) {
                this.rightItemField.setText(this.initialData.right.item);
                this.rightConditionField.setText(this.initialData.right.count.condition);
                this.rightConditionValueField.setText(this.initialData.right.count.value.toString());
            }
        }
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, WIDTH, HEIGHT, 512, 256);

        int x1 = i+16, x2 = i+144, x3 = i+184;
        int y1 = j+64, y2 = j+96, y3 = j+128;

        Identifier leftItemTexture            = this.leftItemError                                                                 ? TEXT_FIELD_LONG_ERROR_TEXTURE  : TEXT_FIELD_LONG_TEXTURE;
        Identifier left2ItemTexture           = this.left2ItemError           || this.left2ItemField.getText().isEmpty()           ? TEXT_FIELD_LONG_ERROR_TEXTURE  : TEXT_FIELD_LONG_TEXTURE;
        Identifier rightItemTexture           = this.rightItemError                                                                ? TEXT_FIELD_LONG_ERROR_TEXTURE  : TEXT_FIELD_LONG_TEXTURE;
        Identifier leftConditionTexture       = this.leftConditionError                                                            ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;
        Identifier left2ConditionTexture      = this.left2ConditionError      || this.left2ConditionField.getText().isEmpty()      ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;
        Identifier rightConditionTexture      = this.rightConditionError                                                           ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;
        Identifier leftConditionValueTexture  = this.leftConditionValueError                                                       ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;
        Identifier left2ConditionValueTexture = this.left2ConditionValueError || this.left2ConditionValueField.getText().isEmpty() ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;
        Identifier rightConditionValueTexture = this.rightConditionValueError                                                      ? TEXT_FIELD_SHORT_ERROR_TEXTURE : TEXT_FIELD_SHORT_TEXTURE;

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, leftItemTexture,            x1, y1, 112, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, left2ItemTexture,           x1, y2, 112, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, rightItemTexture,           x1, y3, 112, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, leftConditionTexture,       x2, y1,  24, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, left2ConditionTexture,      x2, y2,  24, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, rightConditionTexture,      x2, y3,  24, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, leftConditionValueTexture,  x3, y1,  24, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, left2ConditionValueTexture, x3, y2,  24, 16);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, rightConditionValueTexture, x3, y3,  24, 16);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        context.drawText(this.textRenderer, this.title, i + WIDTH / 2 - this.textRenderer.getWidth(this.title) / 2, j+6, -12566464, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context, delta, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);

        for (Slot slot : this.slots) {
            slot.render(context, this.textRenderer, mouseX, mouseY);
        }

        this.drawForeground(context, mouseX, mouseY);
    }

    @Override
    public void tick() {
        this.leftItemError = false;
        this.left2ItemError = false;
        this.rightItemError = false;
        this.leftConditionError = false;
        this.left2ConditionError = false;
        this.rightConditionError = false;
        this.leftConditionValueError = false;
        this.left2ConditionValueError = false;
        this.rightConditionValueError = false;

        if (!this.leftItemField.isValid())
            this.leftItemError = true;
        if (!this.left2ItemField.getText().isEmpty() && !this.left2ItemField.isValid())
            this.left2ItemError = true;
        if (!this.rightItemField.isValid())
            this.rightItemError = true;

        if (!this.leftConditionField.isValid())
            this.leftConditionError = true;
        if (!this.left2ConditionField.getText().isEmpty() && !this.left2ConditionField.isValid())
            this.left2ConditionError = true;
        if (!this.rightConditionField.isValid())
            this.rightConditionError = true;

        if (!this.leftConditionValueField.isValid())
            this.leftConditionValueError = true;
        if (!this.left2ConditionValueField.getText().isEmpty() && !this.left2ConditionValueField.isValid())
            this.left2ConditionValueError = true;
        if (!this.rightConditionValueField.isValid())
            this.rightConditionValueError = true;

        if (
            this.leftItemError
                || this.left2ItemError
                || this.rightItemError
                || this.leftConditionError
                || this.left2ConditionError
                || this.rightConditionError
                || this.leftConditionValueError
                || this.left2ConditionValueError
                || this.rightConditionValueError
        ) {
            this.confirmButton.active = false;
        } else {
            this.confirmButton.active = true;
        }

        this.setSlotStack(0, this.leftItemField.isValid() ? getValidItemStack(this.leftItemField.getText()) : null);
        this.setSlotStack(1, this.left2ItemField.isValid() ? getValidItemStack(this.left2ItemField.getText()) : null);
        this.setSlotStack(2, this.rightItemField.isValid() ? getValidItemStack(this.rightItemField.getText()) : null);

        this.setSlotCondition(0, this.leftConditionError || this.leftConditionValueError ? null : new Condition(this.leftConditionField.getText(), Integer.parseInt(this.leftConditionValueField.getText())));
        this.setSlotCondition(1, (
            this.left2ConditionError
                || this.left2ConditionValueError
                || !this.left2ConditionField.getText().isEmpty()
                || !this.left2ConditionValueField.getText().isEmpty()
                || !this.left2ConditionField.isValid()
                || !this.left2ConditionValueField.isValid()
        ) ? null : new Condition(this.left2ConditionField.getText(), Integer.parseInt(this.left2ConditionValueField.getText())));
        this.setSlotCondition(2, this.rightConditionError || this.rightConditionValueError ? null : new Condition(this.rightConditionField.getText(), Integer.parseInt(this.rightConditionValueField.getText())));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String leftItemFieldValue = this.leftItemField.getText();
        String leftConditionFieldValue = this.leftConditionField.getText();
        String leftConditionValueFieldValue = this.leftConditionValueField.getText();
        String left2ItemFieldValue = this.left2ItemField.getText();
        String left2ConditionFieldValue = this.left2ConditionField.getText();
        String left2ConditionValueFieldValue = this.left2ConditionValueField.getText();
        String rightItemFieldValue = this.rightItemField.getText();
        String rightConditionFieldValue = this.rightConditionField.getText();
        String rightConditionValueFieldValue = this.rightConditionValueField.getText();

        this.init(client, width, height);

        this.leftItemField.setText(leftItemFieldValue);
        this.leftConditionField.setText(leftConditionFieldValue);
        this.leftConditionValueField.setText(leftConditionValueFieldValue);
        this.left2ItemField.setText(left2ItemFieldValue);
        this.left2ConditionField.setText(left2ConditionFieldValue);
        this.left2ConditionValueField.setText(left2ConditionValueFieldValue);
        this.rightItemField.setText(rightItemFieldValue);
        this.rightConditionField.setText(rightConditionFieldValue);
        this.rightConditionValueField.setText(rightConditionValueFieldValue);
    }

    public void setSlotStack(int index, @Nullable ItemStack stack) {
        if (index >= 0 && index < this.slots.size()) {
            this.slots.get(index).setStack(stack);
        }
    }

    public void setSlotCondition(int index, @Nullable Condition condition) {
        if (index >= 0 && index < this.slots.size()) {
            this.slots.get(index).setCondition(condition);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    private static boolean isValidItem(String idString) {
        idString = idString.contains(":") ? idString : "minecraft:" + idString;
        if (idString.equals("minecraft:air")) {
            return true;
        }
        try {
            Identifier id = Identifier.of(idString);
            Item item = Registries.ITEM.get(id);
            return item != Items.AIR;
        } catch (Exception e) {
            return false;
        }
    }

    private static @Nullable ItemStack getValidItemStack(String idString) {
        idString = idString.contains(":") ? idString : "minecraft:" + idString;
        if (idString.equals("minecraft:air")) {
            return null;
        }
        try {
            Identifier id = Identifier.of(idString);
            Item item = Registries.ITEM.get(id);
            if (item == Items.AIR) {
                return null;
            }
            return item.getDefaultStack();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getValidItemString(String idString) {
        return idString.contains(":") ? idString : "minecraft:" + idString;
    }

    private static boolean isValidConditionValue(String s) {
            if (s == null || s.isEmpty()) {
                return false;
            }
            try {
                return Integer.parseInt(s) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

    @Environment(EnvType.CLIENT)
    abstract static class BaseButtonWidget extends PressableWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            super(x, y, 22, 22, ScreenTexts.EMPTY);
        }

        protected BaseButtonWidget(int x, int y, Text message) {
            super(x, y, 22, 22, message);
        }

        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            Identifier identifier;
            if (!this.active) {
                identifier = VillagerTradeEditorScreen.BUTTON_DISABLED_TEXTURE;
            } else if (this.disabled) {
                identifier = VillagerTradeEditorScreen.BUTTON_SELECTED_TEXTURE;
            } else if (this.isSelected()) {
                identifier = VillagerTradeEditorScreen.BUTTON_HIGHLIGHTED_TEXTURE;
            } else {
                identifier = VillagerTradeEditorScreen.BUTTON_TEXTURE;
            }

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
            this.renderExtra(context);
        }

        protected abstract void renderExtra(DrawContext context);

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }
    }

    @Environment(EnvType.CLIENT)
    abstract static class IconButtonWidget extends VillagerTradeEditorScreen.BaseButtonWidget {
        private final Identifier texture;

        protected IconButtonWidget(int x, int y, Identifier texture, Text message) {
            super(x, y, message);
            this.texture = texture;
        }

        protected void renderExtra(DrawContext context) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX() + 2, this.getY() + 2, 18, 18);
        }
    }

    @Environment(EnvType.CLIENT)
    class CancelButtonWidget extends VillagerTradeEditorScreen.IconButtonWidget {
        public CancelButtonWidget(final int x, final int y) {
            super(x, y, VillagerTradeEditorScreen.CANCEL_TEXTURE, ScreenTexts.CANCEL);
        }

        public void onPress() {
            VillagerTradeEditorScreen.this.close();
        }

    }

    @Environment(EnvType.CLIENT)
    class ConfirmButtonWidget extends VillagerTradeEditorScreen.IconButtonWidget {
        public ConfirmButtonWidget(final int x, final int y) {
            super(x, y, VillagerTradeEditorScreen.CONFIRM_TEXTURE, ScreenTexts.DONE);
        }

        public void onPress() {
            // VillagerTradeAddScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(Optional.ofNullable(VillagerTradeAddScreen.this.primaryEffect), Optional.ofNullable(VillagerTradeAddScreen.this.secondaryEffect)));
            // TODO добавление данных

            TradeRule newRule = new TradeRule(
                List.of("minecraft:farmer"),
                new TradeItemSide(
                    getValidItemString(VillagerTradeEditorScreen.this.leftItemField.getText()),
                    new Condition(
                        VillagerTradeEditorScreen.this.leftConditionField.getText(),
                        Integer.parseInt(VillagerTradeEditorScreen.this.leftConditionValueField.getText())
                    )
                ),
                VillagerTradeEditorScreen.this.left2ItemField.getText().isEmpty() ? null : new TradeItemSide(
                    getValidItemString(VillagerTradeEditorScreen.this.left2ItemField.getText()),
                    new Condition(
                        VillagerTradeEditorScreen.this.left2ConditionField.getText(),
                        Integer.parseInt(VillagerTradeEditorScreen.this.left2ConditionValueField.getText())
                    )
                ),
                new TradeItemSide(
                    getValidItemString(VillagerTradeEditorScreen.this.rightItemField.getText()),
                    new Condition(
                        VillagerTradeEditorScreen.this.rightConditionField.getText(),
                        Integer.parseInt(VillagerTradeEditorScreen.this.rightConditionValueField.getText())
                    )
                )
            );

            if (VillagerTradeEditorScreen.this.initialData != null) {
                // update
                if (VillagerTradeEditorScreen.this.initialDataIndex < AutoVillagerTraderModClient.CONFIG.trades.size()) {
                    AutoVillagerTraderModClient.CONFIG.trades.set(VillagerTradeEditorScreen.this.initialDataIndex, newRule);
                }
            } else {
                // add
                AutoVillagerTraderModClient.CONFIG.trades.add(newRule);
            }
            ConfigManager.saveConfig();

            VillagerTradeEditorScreen.this.close();
        }
    }

    @Environment(EnvType.CLIENT)
    static class ColoredTextFieldWidget extends TextFieldWidget {
        private int focusedColor = -2039584;
        private int unfocusedColor = -9408400;
        private final Function<String, Boolean> isValidFunc;

        public ColoredTextFieldWidget(
            TextRenderer textRenderer,
            int x,
            int y,
            int width,
            int height,
            Text text,
            Function<String, Boolean> isValidFunc
        ) {
            super(textRenderer, x, y, width, height, text);
            this.isValidFunc = isValidFunc;
        }

        public boolean isValid() {
            return !this.getText().isEmpty() && this.isValidFunc.apply(this.getText());
        }

        public void setFocusedColor(int color) {
            this.focusedColor = color;
        }

        public void setUnfocusedColor(int color) {
            this.unfocusedColor = color;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.isValid()) {
                this.setEditableColor(focusedColor);
            } else {
                this.setEditableColor(unfocusedColor);
            }
            super.renderWidget(context, mouseX, mouseY, delta);
        }
    }
}
