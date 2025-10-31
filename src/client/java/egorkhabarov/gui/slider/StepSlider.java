package egorkhabarov.gui.slider;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class StepSlider extends SliderWidget {
    private final double min;
    private final double max;
    private final double step;
    private final Text name;
    private final String suffix;
    private double valueReal;
    private final Consumer<Double> onChanged;

    public StepSlider(int x, int y, int width, int height,
                      Text name, String suffix, double min, double max, double step, double initial,
                      Consumer<Double> onChanged) {
        super(x, y, width, height, Text.literal(""), (initial - min) / (max - min));
        this.name = name;
        this.suffix = suffix;
        this.min = min;
        this.max = max;
        this.step = step;
        this.onChanged = onChanged;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.valueReal = getRealValue();
        if (this.suffix.equals("s")) {
            this.setMessage(this.name.copy().append(": ").append(String.valueOf(this.valueReal/1000)).append(this.suffix));
        } else {
            this.setMessage(this.name.copy().append(": ").append(String.valueOf(this.valueReal)).append(this.suffix));
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        this.onChanged.accept(this.valueReal);
    }

    @Override
    protected void applyValue() {
        this.valueReal = getRealValue();
    }

    private double getRealValue() {
        double raw = this.min + this.value * (this.max - this.min);
        return Math.round(raw / this.step) * this.step;
    }
}
