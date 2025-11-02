package egorkhabarov.gui;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.ConfigManager;
import egorkhabarov.gui.slider.StepSlider;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModSettingsScreen extends TabScreen {
    private static final Identifier TEXTURE = Identifier.of("auto_villager_trader", "textures/gui/empty_screen.png");

    public ModSettingsScreen(Screen parent) {
        super(Text.translatable("avt_menu.settings"), parent);
    }

    public ModSettingsScreen() {
        super(Text.translatable("avt_menu.settings"), null);
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;
        ConfigData config = AutoVillagerTraderModClient.CONFIG;

        {
            this.addDrawableChild(
                CheckboxWidget.builder(
                        Text.translatable("avt_menu.button.enabled"),
                        this.textRenderer
                    )
                    .pos(i + 10, j + 10 + 20)
                    .checked(config.enabled)
                    .maxWidth(75)
                    .callback((CheckboxWidget c, boolean checked) -> {
                        config.enabled = checked;
                        ConfigManager.saveConfig();
                    })
                    .build()
            );
            this.addDrawableChild(
                CheckboxWidget.builder(
                        Text.translatable("avt_menu.button.auto_finder_enabled"),
                        this.textRenderer
                    )
                    .pos(i + 10, j + 10 + 20 + 25)
                    .checked(config.auto_finder_enabled)
                    .maxWidth(75)
                    .callback((CheckboxWidget c, boolean checked) -> {
                        config.auto_finder_enabled = checked;
                        ConfigManager.saveConfig();
                    })
                    .tooltip(Tooltip.of(Text.translatable("avt_menu.toast.auto_finder")))
                    .build()
            );
            this.addDrawableChild(
                CheckboxWidget.builder(
                        Text.translatable("avt_menu.button.need_see"),
                        this.textRenderer
                    )
                    .pos(i + 10, j + 10 + 20 + 50)
                    .checked(config.need_see)
                    .maxWidth(75)
                    .callback((CheckboxWidget c, boolean checked) -> {
                        config.need_see = checked;
                        ConfigManager.saveConfig();
                    })
                    .tooltip(Tooltip.of(Text.translatable("avt_menu.toast.need_see")))
                    .build()
            );
        }

        this.addDrawableChild(new StepSlider(
            i + 116,
            j + 10 + 20,
            150, 18,
            Text.translatable("avt_menu.button.scan_interval_ms"),
            "s",
            100, 10_000, 100, // min, max, step
            config.scan_interval_ms, // initial value
            (Double value) -> {
                config.scan_interval_ms = value.longValue();
                ConfigManager.saveConfig();
            }
        ));

        this.addDrawableChild(new StepSlider(
            i + 116,
            j + 10 + 20 + 25,
            150, 18,
            Text.translatable("avt_menu.button.scan_radius"),
            "",
            0.5, 7, 0.5,
            config.scan_radius,
            (Double value) -> {
                config.scan_radius = value;
                ConfigManager.saveConfig();
            }
        ));

        this.addDrawableChild(new StepSlider(
            i + 116,
            j + 10 + 20 + 50,
            150, 18,
            Text.translatable("avt_menu.button.villager_cache_ttl"),
            "s",
            100, 20_000, 100,
            config.villager_cache_ttl,
            (Double value) -> {
                config.villager_cache_ttl = value.longValue();
                ConfigManager.saveConfig();
            }
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = (this.width - this.WIDTH) / 2;
        int j = (this.height - this.HEIGHT) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.WIDTH, this.HEIGHT, 512, 256);
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawText(this.textRenderer, Text.translatable("avt_menu.settings"), i+10, j+12, -12566464, false);
    }
}
