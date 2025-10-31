package egorkhabarov.gui;

import egorkhabarov.gui.tab.Tab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AbstractTabScreen extends Screen {
    protected final Screen parent;
    protected final int WIDTH = 276;
    protected final int HEIGHT = 166;
    protected final List<Tab> tabs = new ArrayList<>();
    protected int selectedTab;

    public AbstractTabScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        for (Tab tab : this.tabs) {
            tab.render(context, mouseX, mouseY, deltaTicks);
        }
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Tab tab : this.tabs) {
                if (tab.mouseClicked(mouseX, mouseY, button)) {
                    this.setSelectedTab(tab.getIndex());
                    if (this.client != null) {
                        this.client.setScreen(tab.getTarget());
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setSelectedTab(int index) {
        this.selectedTab = index;
        for (Tab tab : this.tabs) {
            tab.setSelected(false);
        }
        this.tabs.get(index).setSelected(true);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
