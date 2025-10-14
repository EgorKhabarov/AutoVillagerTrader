package egorkhabarov.config;

import egorkhabarov.logic.ConditionChecker;
import net.minecraft.village.TradeOffer;

public class TradeRule {
    public boolean enabled = true;
    public long cooldown_ms = 0;
    public String comment;
    public TradeItemSide left;   // то, что игрок отдаёт
    public TradeItemSide left2;  // если у сделки две вещи слева (опционально)
    public TradeItemSide right;  // то, что игрок получает

    public boolean matchOffer(TradeOffer offer) {
        String default_item = "minecraft:air";
        return (this.left != null ? this.left.item : default_item).equals(offer.getDisplayedFirstBuyItem().getItem().toString())
            && (this.left2 != null ? this.left2.item : default_item).equals(offer.getDisplayedSecondBuyItem().getItem().toString())
            && (this.right != null ? this.right.item : default_item).equals(offer.getSellItem().getItem().toString())
            && (ConditionChecker.match(this.left.count, offer.getDisplayedFirstBuyItem().getCount()));

    }
}
