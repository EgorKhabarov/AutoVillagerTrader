package egorkhabarov.config;

import egorkhabarov.logic.ConditionChecker;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradeRule {
    public boolean enabled = true;
    public long cooldown_ms = 0;
    public List<String> professions;
    public TradeItemSide left;
    public @Nullable TradeItemSide left2;  // Optional
    public TradeItemSide right;

    public boolean matchOffer(TradeOffer offer) {
        String default_item = "minecraft:air";
        return (this.left != null ? this.left.item : default_item).equals(offer.getDisplayedFirstBuyItem().getItem().toString())
            && (this.left2 != null ? this.left2.item : default_item).equals(offer.getDisplayedSecondBuyItem().getItem().toString())
            && (this.right != null ? this.right.item : default_item).equals(offer.getSellItem().getItem().toString())
            && (ConditionChecker.match(this.left.count, offer.getDisplayedFirstBuyItem().getCount()));
    }

    public String getRuleId() {
        String result = "";
        if (left != null) {
            result += left.getTradeId() + "_";
        }
        if (left2 != null) {
            result += left2.getTradeId() + "_";
        }
        if (right != null) {
            result += right.getTradeId();
        }
        return result;
    }
}
