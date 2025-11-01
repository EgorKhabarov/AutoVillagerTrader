package egorkhabarov.gui;

import egorkhabarov.gui.tab.Tab;
import egorkhabarov.gui.tab.TabType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TabScreen extends AbstractTabScreen {
    public TabScreen(Text title, Screen parent) {
        super(title, parent);
    }

    @Override
    protected void init() {
        int i = (this.width - this.WIDTH) / 2;
        int j = (this.height - this.HEIGHT) / 2;

        this.tabs.clear();
        this.tabs.add(
            new Tab(
                0,
                TabType.LEFT,
                i, j,
                new ModSettingsScreen(this.parent),
                Items.COMMAND_BLOCK
            )
        );
        this.tabs.add(
            new Tab(
                1,
                TabType.MIDDLE,
                i, j,
                new VillagerTradeSettingsScreen(this.parent),
                Items.EMERALD
            )
        );
        this.tabs.add(
            new Tab(
                2,
                TabType.MIDDLE,
                i, j,
                new VillagersSettingsScreen(this.parent),
                Items.VILLAGER_SPAWN_EGG
            )
        );
        for (Tab tab : tabs) {
            tab.setSelected(tab.getTarget().getClass() == this.getClass());
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
