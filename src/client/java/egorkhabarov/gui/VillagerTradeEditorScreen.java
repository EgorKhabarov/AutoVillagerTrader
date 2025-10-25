package egorkhabarov.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.TradeRule;

import java.util.List;

@Environment(EnvType.CLIENT)
public class VillagerTradeEditorScreen extends Screen {

    private static final Identifier TEXTURE = Identifier.of("auto_villager_trader", "textures/gui/villager_trade_editor.png");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/villager/scroller_disabled");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int WIDTH = 276;
    private static final int HEIGHT = 166;
    private static final int TRADE_LIST_X = 5;
    private static final int TRADE_LIST_Y = 18;
    private static final int TRADE_OFFER_HEIGHT = 20;
    private static final int MAX_VISIBLE_TRADES = 7;
    private final Screen parent;

    private int indexStartOffset = 0;
    private boolean scrolling = false;

    private List<TradeRule> trades;
    private int selectedIndex = -1;

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
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;

        // Кнопки управления
        addDrawableChild(ButtonWidget.builder(Text.literal("✎"), b -> onEdit())
                .dimensions(x + 150, y + 100, 20, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> onAdd())
                .dimensions(x + 175, y + 100, 20, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("🗑"), b -> onDelete())
                .dimensions(x + 200, y + 100, 20, 20)
                .build());
    }

    private void onEdit() {
        if (selectedIndex >= 0 && selectedIndex < trades.size()) {
            TradeRule rule = trades.get(selectedIndex);
            rule.enabled = !rule.enabled;
            // ConfigManager.saveConfig();
        }
    }

    private void onAdd() {
        TradeRule newRule = new TradeRule();
        trades.add(newRule);
        // ConfigManager.saveConfig();
        reloadTrades();
    }

    private void onDelete() {
        if (selectedIndex >= 0 && selectedIndex < trades.size()) {
            trades.remove(selectedIndex);
            // ConfigManager.saveConfig();
            reloadTrades();
        }
    }

    private void reloadTrades() {
        this.trades = AutoVillagerTraderModClient.CONFIG.trades;
        this.selectedIndex = -1;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;

        // Фон
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        drawTrades(context, x, y, mouseX, mouseY);
        drawScrollbar(context, x, y);
        drawTitle(context, x, y);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTitle(DrawContext context, int x, int y) {
        context.drawCenteredTextWithShadow(textRenderer, title, x + WIDTH / 2, y + 6, 0xFFFFFF);
    }

    private void drawTrades(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int offsetY = y + TRADE_LIST_Y;
        int startIndex = indexStartOffset;
        int endIndex = Math.min(startIndex + MAX_VISIBLE_TRADES, trades.size());

        for (int i = startIndex; i < endIndex; i++) {
            int drawY = offsetY + (i - startIndex) * TRADE_OFFER_HEIGHT;
            TradeRule rule = trades.get(i);

            // Подсветка выбранного
            if (i == selectedIndex) {
                context.fill(x + TRADE_LIST_X, drawY - 2, x + 90, drawY + 18, 0xFFAAAAAA);
            }

            // Отрисовка текста
            String left = rule.left != null ? rule.left.item : "?";
            String right = rule.right != null ? rule.right.item : "?";
            String text = left + " → " + right + (rule.enabled ? "" : " (off)");

            context.drawText(textRenderer, text, x + 10, drawY + 4, rule.enabled ? 0xFFFFFF : 0x888888, false);
        }
    }

    private void drawScrollbar(DrawContext context, int x, int y) {
        int total = trades.size();
        if (total <= MAX_VISIBLE_TRADES) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED_TEXTURE, x + 94, y + 18, 6, 27);
            return;
        }

        int scrollArea = 139;
        int visible = MAX_VISIBLE_TRADES;
        int hidden = total - visible;
        int scrollHeight = MathHelper.clamp(scrollArea * visible / total, 10, scrollArea - 10);
        int scrollY = y + 18 + (scrollArea - scrollHeight) * indexStartOffset / hidden;

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, x + 94, scrollY, 6, 27);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;

        // Проверяем клик по списку трейдов
        int startY = y + TRADE_LIST_Y;
        int endY = startY + MAX_VISIBLE_TRADES * TRADE_OFFER_HEIGHT;

        if (mouseX >= x + TRADE_LIST_X && mouseX <= x + 100 && mouseY >= startY && mouseY <= endY) {
            int clickedIndex = (int) ((mouseY - startY) / TRADE_OFFER_HEIGHT) + indexStartOffset;
            if (clickedIndex < trades.size()) {
                selectedIndex = clickedIndex;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int total = trades.size();
        if (total > MAX_VISIBLE_TRADES) {
            int hidden = total - MAX_VISIBLE_TRADES;
            indexStartOffset = MathHelper.clamp(indexStartOffset - (int) verticalAmount, 0, hidden);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
