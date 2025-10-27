package egorkhabarov.config;

import egorkhabarov.logic.ConditionChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class TradeItemSide {
    public String item;
    public Condition count;

    public TradeItemSide(String item, Condition count) {
        this.item = item;
        this.count = count;
    }

    public String getTradeId() {
        return item.replace("minecraft:", "")+"_"+count.getConditionId();
    }

    public boolean match(ItemStack stack) {
        Identifier stackId = Registries.ITEM.getId(stack.getItem());
        return Objects.equals(stackId.toString(), this.item)
            && ConditionChecker.match(this.count, stack.getCount());
    }
}
