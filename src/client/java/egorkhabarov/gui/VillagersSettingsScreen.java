package egorkhabarov.gui;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.ConfigManager;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VillagersSettingsScreen extends TabScreen {
    private static final Identifier TEXTURE = Identifier.of("auto_villager_trader", "textures/gui/empty_screen.png");
    public static final MutableText[] professions = {
        Text.translatable("entity.minecraft.villager.armorer"),
        Text.translatable("entity.minecraft.villager.butcher"),
        Text.translatable("entity.minecraft.villager.cartographer"),
        Text.translatable("entity.minecraft.villager.cleric"),
        Text.translatable("entity.minecraft.villager.farmer"),

        Text.translatable("entity.minecraft.villager.fisherman"),
        Text.translatable("entity.minecraft.villager.fletcher"),
        Text.translatable("entity.minecraft.villager.leatherworker"),
        Text.translatable("entity.minecraft.villager.librarian"),
        Text.translatable("entity.minecraft.villager.mason"),

        Text.translatable("entity.minecraft.villager.shepherd"),
        Text.translatable("entity.minecraft.villager.toolsmith"),
        Text.translatable("entity.minecraft.villager.weaponsmith")
    };
    public static final String[] professionIds = {
        "minecraft:armorer",
        "minecraft:butcher",
        "minecraft:cartographer",
        "minecraft:cleric",
        "minecraft:farmer",

        "minecraft:fisherman",
        "minecraft:fletcher",
        "minecraft:leatherworker",
        "minecraft:librarian",
        "minecraft:mason",

        "minecraft:shepherd",
        "minecraft:toolsmith",
        "minecraft:weaponsmith"
    };

    public VillagersSettingsScreen(Screen parent) {
        super(Text.translatable("avt_menu.villagers"), parent);
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - WIDTH) / 2;
        int j = (this.height - HEIGHT) / 2;

        int now_professions = 0;
        int max_professions_index = professions.length-1;
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        for (int x = 0;x < 3;x++) {
            for (int y = 0;y < 5;y++) {
                if (now_professions > max_professions_index) {
                    break;
                }
                int dx = (79+10)*x, dy = (20+5)*y;
                final int index = now_professions;
                this.addDrawableChild(
                    CheckboxWidget.builder(
                        professions[now_professions],
                        this.textRenderer
                    )
                        .pos(i + 10 + dx, j + 10 + 20 + dy)
                        .checked(config.professions.contains(professionIds[now_professions]))
                        .maxWidth(65)
                        .callback((c, checked) -> {
                            String professionId = professionIds[index];
                            if (checked && !config.professions.contains(professionId)) {
                                config.professions.add(professionId);
                            } else {
                                config.professions.remove(professionId);
                            }
                            ConfigManager.saveConfig();
                        })
                        .build()
                );
                now_professions++;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = (this.width - this.WIDTH) / 2;
        int j = (this.height - this.HEIGHT) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.WIDTH, this.HEIGHT, 512, 256);
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawText(this.textRenderer, Text.translatable("avt_menu.select_professions"), i+10, j+12, -12566464, false);
    }
}
