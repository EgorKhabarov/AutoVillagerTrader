package egorkhabarov.gui;

import egorkhabarov.config.Condition;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.gui.slot.Slot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.TradeRule;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class VillagerTradeEditorScreen extends Screen {
    private static final Identifier OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla("container/villager/out_of_stock");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla("container/villager/trade_arrow_out_of_stock");
    private static final Identifier TRADE_ARROW_TEXTURE = Identifier.ofVanilla("container/villager/trade_arrow");
    private static final Identifier DISCOUNT_STRIKETHROUGH_TEXTURE = Identifier.ofVanilla("container/villager/discount_strikethrough");
    private static final Identifier TEXTURE = Identifier.of("auto_villager_trader", "textures/gui/villager_trade_editor.png");
    private static final Text TRADES_TEXT = Text.translatable("merchant.trades");
    private int selectedIndex;
    private final WidgetButtonPage[] offers = new WidgetButtonPage[7];
    int indexStartOffset;
    private boolean scrolling;
    private static final int WIDTH = 276;
    private static final int HEIGHT = 166;
    private final Screen parent;
    private final List<TradeRule> trades;
    private final List<Slot> slots = new ArrayList<>();

    public VillagerTradeEditorScreen(Screen parent) {
        super(Text.translatable("avt_menu.trades"));
        this.parent = parent;
        this.trades = AutoVillagerTraderModClient.CONFIG.trades;
    }

    public VillagerTradeEditorScreen() {
        super(Text.translatable("avt_menu.trades"));
        this.parent = null;
        this.trades = AutoVillagerTraderModClient.CONFIG.trades;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            this.offers[l] = this.addDrawableChild(new WidgetButtonPage(i + 5, k, l, (button) -> {
                if (button instanceof WidgetButtonPage) {
                    this.selectedIndex = ((WidgetButtonPage)button).getIndex() + this.indexStartOffset;
                }
            }));
            k += 20;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("✎"), b -> onEdit())
            .dimensions(i + 150, j + 100, 20, 20)
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> onAdd())
            .dimensions(i + 175, j + 100, 20, 20)
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("🗑"), b -> onDelete())
            .dimensions(i + 200, j + 100, 20, 20)
            .build());

        this.slots.clear();
        this.slots.add(new Slot(i + 136, j + 37, null));
        this.slots.add(new Slot(i + 162, j + 37, null));
        this.slots.add(new Slot(i + 220, j + 37, null));
    }

    private void onEdit() {
        if (selectedIndex >= 0 && selectedIndex < trades.size()) {
            TradeRule rule = this.trades.get(selectedIndex);
            rule.enabled = !rule.enabled;
            ConfigManager.saveConfig();
        }
    }

    private void onAdd() {
        this.indexStartOffset = 0;
        this.selectedIndex = 0;

        if (this.client != null) {
            this.client.setScreen(new VillagerTradeAddScreen(this));
        }
    }

    private void onDelete() {
        if (selectedIndex >= 0 && selectedIndex < trades.size()) {
            this.trades.remove(selectedIndex);
            ConfigManager.saveConfig();
            this.indexStartOffset = 0;
            this.selectedIndex = 0;
        }
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        context.drawText(this.textRenderer, this.title, i+49 + WIDTH / 2 - this.textRenderer.getWidth(this.title) / 2, j+6, -12566464, false);

        TradeRule tradeRule = this.trades.get(this.selectedIndex);
        context.drawText(this.textRenderer, Text.of(tradeRule.getRuleId()), i+107, j+HEIGHT-94, -12566464, false);
        int l = this.textRenderer.getWidth(TRADES_TEXT);
        context.drawText(this.textRenderer, TRADES_TEXT, i+5 - l / 2 + 48, j+6, -12566464, false);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, WIDTH, HEIGHT, 512, 256);
        if (!this.trades.isEmpty()) {
            int k = this.selectedIndex;
            if (k < 0 || k >= this.trades.size()) {
                return;
            }

            TradeRule tradeRule = this.trades.get(k);
            if (!tradeRule.enabled) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OUT_OF_STOCK_TEXTURE, i + 83 + 99, j + 35, 28, 21);
            }
            this.setSlotItems(tradeRule);
        }
    }

    private void renderScrollbar(DrawContext context, int x, int y, List<TradeRule> tradeRules) {
        int i = tradeRules.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, x + 94, y + 18 + m, 6, 27);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED_TEXTURE, x + 94, y + 18, 6, 27);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.drawBackground(context, deltaTicks, mouseX, mouseY);
        super.render(context, mouseX, mouseY, deltaTicks);

        if (!this.trades.isEmpty()) {
            int i = (this.width - WIDTH) / 2;
            int j = (this.height - HEIGHT) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScrollbar(context, i, j, this.trades);
            int m = 0;

            for(TradeRule tradeRule : this.trades) {
                if (!this.canScroll(this.trades.size()) || m >= this.indexStartOffset && m < 7 + this.indexStartOffset) {
                    ItemStack itemStack = tradeRule.getFirstBuyItem();
                    ItemStack itemStack2 = tradeRule.getFirstBuyItem();
                    ItemStack itemStack3 = tradeRule.getSecondBuyItem();
                    ItemStack itemStack4 = tradeRule.getSellItem();
                    int n = k + 2;
                    this.renderFirstBuyItem(context, itemStack2, itemStack, tradeRule, l, n);
                    if (!itemStack3.isEmpty() && tradeRule.left2 != null && tradeRule.left2.count != null) {
                        context.drawItemWithoutEntity(itemStack3, i + 5 + 35, n);
                        // context.drawStackOverlay(this.textRenderer, itemStack3, i + 5 + 35, n);
                        Slot.renderItem(context, this.textRenderer, itemStack3, tradeRule.left2.count, i + 5 + 35, n);
                    }

                    this.renderArrow(context, tradeRule.enabled, i, n);
                    context.drawItemWithoutEntity(itemStack4, i + 5 + 68, n);
                    // context.drawStackOverlay(this.textRenderer, itemStack4, i + 5 + 68, n);
                    Slot.renderItem(context, this.textRenderer, itemStack4, tradeRule.right.count, i + 5 + 68, n);
                    k += 20;
                    ++m;
                } else {
                    ++m;
                }
            }

            for (WidgetButtonPage widgetButtonPage : this.offers) {
                if (widgetButtonPage.isSelected()) {
                    widgetButtonPage.renderTooltip(context, mouseX, mouseY);
                }

                widgetButtonPage.visible = widgetButtonPage.index < this.trades.size();
            }
        }

        for (Slot slot : this.slots) {
            slot.render(context, this.textRenderer, mouseX, mouseY);
        }

        this.drawForeground(context, mouseX, mouseY);
    }

    public void setSlotItem(int index, ItemStack stack, Condition condition) {
        if (index >= 0 && index < this.slots.size()) {
            this.slots.get(index).setStack(stack);
            this.slots.get(index).setCondition(condition);
        }
    }

    public void setSlotItems(TradeRule rule) {
        this.setSlotItem(0, rule.getFirstBuyItem(), rule.left.count);
        this.setSlotItem(1, rule.getSecondBuyItem(), rule.left2 != null ? rule.left2.count : null);
        this.setSlotItem(2, rule.getSellItem(), rule.right.count);
    }

    private void renderArrow(DrawContext context, boolean enabled, int x, int y) {
        if (enabled) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_TEXTURE, x + 5 + 35 + 20, y + 3, 10, 9);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_OUT_OF_STOCK_TEXTURE, x + 5 + 35 + 20, y + 3, 10, 9);
        }
    }

    private void renderFirstBuyItem(DrawContext context, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, TradeRule tradeRule, int x, int y) {
        context.drawItemWithoutEntity(adjustedFirstBuyItem, x, y);
        Slot.renderItem(context, this.textRenderer, adjustedFirstBuyItem, tradeRule.left.count, x, y);

        // if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
        //     context.drawStackOverlay(this.textRenderer, adjustedFirstBuyItem, x, y);
        // } else {
        //     context.drawStackOverlay(this.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
        //     context.drawStackOverlay(this.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
        //     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DISCOUNT_STRIKETHROUGH_TEXTURE, x + 7, y + 12, 9, 2);
        // }
    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        } else {
            int i = this.trades.size();
            if (this.canScroll(i)) {
                int j = i - 7;
                this.indexStartOffset = MathHelper.clamp((int)((double)this.indexStartOffset - verticalAmount), 0, j);
            }

            return true;
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.trades.size();
        if (this.scrolling) {
            int j = this.height + 18;  // TODO y?
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.indexStartOffset = MathHelper.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        if (this.canScroll(this.trades.size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.scrolling = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Environment(EnvType.CLIENT)
    class WidgetButtonPage extends ButtonWidget {
        final int index;

        public WidgetButtonPage(final int x, final int y, final int index, final ButtonWidget.PressAction onPress) {
            super(x, y, 88, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderTooltip(DrawContext context, int x, int y) {
            if (this.hovered && VillagerTradeEditorScreen.this.trades.size() > this.index + VillagerTradeEditorScreen.this.indexStartOffset) {
                if (x < this.getX() + 20) {
                    ItemStack itemStack = VillagerTradeEditorScreen.this.trades.get(this.index + VillagerTradeEditorScreen.this.indexStartOffset).getFirstBuyItem();
                    context.drawItemTooltip(VillagerTradeEditorScreen.this.textRenderer, itemStack, x, y);
                } else if (x < this.getX() + 50 && x > this.getX() + 30) {
                    ItemStack itemStack = VillagerTradeEditorScreen.this.trades.get(this.index + VillagerTradeEditorScreen.this.indexStartOffset).getSecondBuyItem();
                    if (!itemStack.isEmpty()) {
                        context.drawItemTooltip(VillagerTradeEditorScreen.this.textRenderer, itemStack, x, y);
                    } else {
                        context.drawTooltip(VillagerTradeEditorScreen.this.textRenderer, Text.translatable("avt_menu.rule_tooltip"), x, y);
                    }
                } else if (x > this.getX() + 65) {
                    ItemStack itemStack = VillagerTradeEditorScreen.this.trades.get(this.index + VillagerTradeEditorScreen.this.indexStartOffset).getSellItem();
                    context.drawItemTooltip(VillagerTradeEditorScreen.this.textRenderer, itemStack, x, y);
                } else {
                    context.drawTooltip(VillagerTradeEditorScreen.this.textRenderer, Text.translatable("avt_menu.rule_tooltip"), x, y);
                }
            }
        }
    }
}
