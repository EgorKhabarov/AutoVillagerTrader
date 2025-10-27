package egorkhabarov.gui;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.Condition;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.config.TradeItemSide;
import egorkhabarov.config.TradeRule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class VillagerTradeAddScreen extends Screen {
    private static final Identifier TEXT_FIELD_LONG_TEXTURE        = Identifier.of("auto_villager_trader", "container/text_field_long");
    private static final Identifier TEXT_FIELD_SHORT_TEXTURE       = Identifier.of("auto_villager_trader", "container/text_field_short");
    private static final Identifier TEXT_FIELD_LONG_ERROR_TEXTURE  = Identifier.of("auto_villager_trader", "container/text_field_long_error");
    private static final Identifier TEXT_FIELD_SHORT_ERROR_TEXTURE = Identifier.of("auto_villager_trader", "container/text_field_short_error");

    private static final Identifier TEXTURE                        = Identifier.of("auto_villager_trader", "textures/gui/villager_trade_add.png");

    static final Identifier CONFIRM_TEXTURE = Identifier.ofVanilla("container/beacon/confirm");
    static final Identifier BUTTON_DISABLED_TEXTURE = Identifier.ofVanilla("container/beacon/button_disabled");
    static final Identifier BUTTON_SELECTED_TEXTURE = Identifier.ofVanilla("container/beacon/button_selected");
    static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/beacon/button_highlighted");
    static final Identifier BUTTON_TEXTURE = Identifier.ofVanilla("container/beacon/button");

    private static final Text TITLE = Text.translatable("avt_menu.add_trade.title");

    private final Screen parent;
    private static final int WIDTH = 276;
    private static final int HEIGHT = 166;

    private VillagerTradeAddScreen.ConfirmButtonWidget confirmButton;

    private TextFieldWidget leftItemField;
    private TextFieldWidget leftConditionField;
    private TextFieldWidget leftConditionValueField;
    private TextFieldWidget left2ItemField;
    private TextFieldWidget left2ConditionField;
    private TextFieldWidget left2ConditionValueField;
    private TextFieldWidget rightItemField;
    private TextFieldWidget rightConditionField;
    private TextFieldWidget rightConditionValueField;

    private boolean leftItemError = false;
    private boolean leftConditionError = false;
    private boolean leftConditionValueError = false;
    private boolean left2ItemError = false;
    private boolean left2ConditionError = false;
    private boolean left2ConditionValueError = false;
    private boolean rightItemError = false;
    private boolean rightConditionError = false;
    private boolean rightConditionValueError = false;

    Set<String> conditionValues = Set.of("=", "==", "<", ">", "<=", ">=");

    public VillagerTradeAddScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    private TextFieldWidget createButton(int x, int y, int width, int height, Text text) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        TextFieldWidget textField = new TextFieldWidget(this.textRenderer, i + x + 3, j + y + 4, width - 7, height - 4, text);
        // textField.setFocusUnlocked(false);
        // textField.setEditableColor(-1);
        // textField.setUneditableColor(-1);
        textField.setDrawsBackground(false);
        // textField.setMaxLength(50);
        // nameField.setChangedListener(this::onRenamed);
        textField.setText("");
        // textField.setEditable(this.);
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

        this.confirmButton = new VillagerTradeAddScreen.ConfirmButtonWidget(i + 164+68, j + 107+15);
        this.addDrawableChild(confirmButton);

        this.leftItemField            = createButton(x1, y1, width1, height1, Text.translatable("container.repair"));
        this.left2ItemField           = createButton(x1, y2, width1, height1, Text.translatable("container.repair"));
        this.rightItemField           = createButton(x1, y3, width1, height1, Text.translatable("container.repair"));
        this.leftConditionField       = createButton(x2, y1, width2, height2, Text.translatable("container.repair"));
        this.left2ConditionField      = createButton(x2, y2, width2, height2, Text.translatable("container.repair"));
        this.rightConditionField      = createButton(x2, y3, width2, height2, Text.translatable("container.repair"));
        this.leftConditionValueField  = createButton(x3, y1, width3, height3, Text.translatable("container.repair"));
        this.left2ConditionValueField = createButton(x3, y2, width3, height3, Text.translatable("container.repair"));
        this.rightConditionValueField = createButton(x3, y3, width3, height3, Text.translatable("container.repair"));

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
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, WIDTH, HEIGHT, 512, 256);

        int x1 = i+16, x2 = i+144, x3 = i+184;
        int y1 = j+64, y2 = j+96, y3 = j+128;

        Identifier leftItemTexture            = this.leftItemError            ? TEXT_FIELD_LONG_TEXTURE  : TEXT_FIELD_LONG_ERROR_TEXTURE;
        Identifier left2ItemTexture           = this.left2ItemError           ? TEXT_FIELD_LONG_TEXTURE  : TEXT_FIELD_LONG_ERROR_TEXTURE;
        Identifier rightItemTexture           = this.rightItemError           ? TEXT_FIELD_LONG_TEXTURE  : TEXT_FIELD_LONG_ERROR_TEXTURE;
        Identifier leftConditionTexture       = this.leftConditionError       ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;
        Identifier left2ConditionTexture      = this.left2ConditionError      ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;
        Identifier rightConditionTexture      = this.rightConditionError      ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;
        Identifier leftConditionValueTexture  = this.leftConditionValueError  ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;
        Identifier left2ConditionValueTexture = this.left2ConditionValueError ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;
        Identifier rightConditionValueTexture = this.rightConditionValueError ? TEXT_FIELD_SHORT_TEXTURE : TEXT_FIELD_SHORT_ERROR_TEXTURE;

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
        // context.drawCenteredTextWithShadow(this.textRenderer, TITLE, 62, 10, 0x1F1F20);
        context.drawText(this.textRenderer, this.title, 49 + WIDTH / 2 - this.textRenderer.getWidth(this.title) / 2, 6, -12566464, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context, delta, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
        this.drawForeground(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.leftItemField.setFocused(false);
        this.left2ItemField.setFocused(false);
        this.rightItemField.setFocused(false);
        this.leftConditionField.setFocused(false);
        this.left2ConditionField.setFocused(false);
        this.rightConditionField.setFocused(false);
        this.leftConditionValueField.setFocused(false);
        this.left2ConditionValueField.setFocused(false);
        this.rightConditionValueField.setFocused(false);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        if (!isValidItem(this.leftItemField.getText())) {
            VillagerTradeAddScreen.this.leftItemError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!this.left2ItemField.getText().isEmpty() && !isValidItem(this.left2ItemField.getText())) {
            VillagerTradeAddScreen.this.left2ItemError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!isValidItem(this.rightItemField.getText())) {
            VillagerTradeAddScreen.this.rightItemError = true;
            this.confirmButton.active = false;
            return;
        }

        if (!conditionValues.contains(this.leftConditionField.getText())) {
            VillagerTradeAddScreen.this.leftConditionError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!this.left2ConditionField.getText().isEmpty() && !conditionValues.contains(this.left2ConditionField.getText())) {
            VillagerTradeAddScreen.this.left2ConditionError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!conditionValues.contains(this.rightConditionField.getText())) {
            VillagerTradeAddScreen.this.rightConditionError = true;
            this.confirmButton.active = false;
            return;
        }

        if (!isValidConditionValue(this.leftConditionValueField.getText())) {
            VillagerTradeAddScreen.this.leftConditionValueError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!this.left2ConditionValueField.getText().isEmpty() && !isValidConditionValue(this.left2ConditionValueField.getText())) {
            VillagerTradeAddScreen.this.left2ConditionValueError = true;
            this.confirmButton.active = false;
            return;
        }
        if (!isValidConditionValue(this.rightConditionValueField.getText())) {
            VillagerTradeAddScreen.this.rightConditionValueError = true;
            this.confirmButton.active = false;
            return;
        }

        this.leftItemError = false;
        this.left2ItemError = false;
        this.rightItemError = false;
        this.leftConditionError = false;
        this.left2ConditionError = false;
        this.rightConditionError = false;
        this.leftConditionValueError = false;
        this.left2ConditionValueError = false;
        this.rightConditionValueError = false;

        this.confirmButton.active = true;
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
                identifier = VillagerTradeAddScreen.BUTTON_DISABLED_TEXTURE;
            } else if (this.disabled) {
                identifier = VillagerTradeAddScreen.BUTTON_SELECTED_TEXTURE;
            } else if (this.isSelected()) {
                identifier = VillagerTradeAddScreen.BUTTON_HIGHLIGHTED_TEXTURE;
            } else {
                identifier = VillagerTradeAddScreen.BUTTON_TEXTURE;
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
    abstract static class IconButtonWidget extends VillagerTradeAddScreen.BaseButtonWidget {
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
    class ConfirmButtonWidget extends VillagerTradeAddScreen.IconButtonWidget {
        public ConfirmButtonWidget(final int x, final int y) {
            super(x, y, VillagerTradeAddScreen.CONFIRM_TEXTURE, ScreenTexts.DONE);
        }

        public void onPress() {
            // VillagerTradeAddScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(Optional.ofNullable(VillagerTradeAddScreen.this.primaryEffect), Optional.ofNullable(VillagerTradeAddScreen.this.secondaryEffect)));
            // TODO добавление данных

            TradeRule newRule = new TradeRule(
                List.of("minecraft:farmer"),
                new TradeItemSide(
                    getValidItemString(VillagerTradeAddScreen.this.leftItemField.getText()),
                    new Condition(
                        VillagerTradeAddScreen.this.leftConditionField.getText(),
                        Integer.parseInt(VillagerTradeAddScreen.this.leftConditionValueField.getText())
                    )
                ),
                VillagerTradeAddScreen.this.left2ItemField.getText().isEmpty() ? null : new TradeItemSide(
                    getValidItemString(VillagerTradeAddScreen.this.left2ItemField.getText()),
                    new Condition(
                        VillagerTradeAddScreen.this.left2ConditionField.getText(),
                        Integer.parseInt(VillagerTradeAddScreen.this.left2ConditionValueField.getText())
                    )
                ),
                new TradeItemSide(
                    getValidItemString(VillagerTradeAddScreen.this.rightItemField.getText()),
                    new Condition(
                        VillagerTradeAddScreen.this.rightConditionField.getText(),
                        Integer.parseInt(VillagerTradeAddScreen.this.rightConditionValueField.getText())
                    )
                )
            );
            AutoVillagerTraderModClient.CONFIG.trades.add(newRule);
            ConfigManager.saveConfig();

            if (VillagerTradeAddScreen.this.client != null && VillagerTradeAddScreen.this.client.player != null) {
                VillagerTradeAddScreen.this.close();
            }
        }
    }
}
