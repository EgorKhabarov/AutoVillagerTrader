package egorkhabarov.config;

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
        if (left == null || !left.match(offer.getDisplayedFirstBuyItem())) {
            return false;
        }
        if (left2 == null || !left2.match(offer.getDisplayedSecondBuyItem())) {
            return false;
        }
        if (right == null || !right.match(offer.getSellItem())) {
            return false;
        }
        return true;
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
