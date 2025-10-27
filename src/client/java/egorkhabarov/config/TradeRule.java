package egorkhabarov.config;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;

public class TradeRule {
    public boolean enabled = true;
    public TradeItemSide left;
    public @Nullable TradeItemSide left2;
    public TradeItemSide right;

    public TradeRule(
        TradeItemSide left,
        @Nullable TradeItemSide left2,
        TradeItemSide right
    ) {
        this.left = left;
        this.left2 = left2;
        this.right = right;
    }

    public boolean matchOffer(TradeOffer offer) {
        if (left == null || !left.match(offer.getDisplayedFirstBuyItem())) {
            return false;
        }
        if (left2 == null || !left2.match(offer.getDisplayedSecondBuyItem())) {
            return false;
        }
        return right != null && right.match(offer.getSellItem());
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

    private static ItemStack getItemStack(TradeItemSide item) {
        if (item == null) {
            return new ItemStack(
                Registries.ITEM.get(Identifier.of("minecraft:air"))
            );
        }
        return new ItemStack(
            Registries.ITEM.get(Identifier.of(item.item)),
            item.count.value
        );
    }

    public ItemStack getFirstBuyItem() {
        return TradeRule.getItemStack(left);
    }

    public ItemStack getSecondBuyItem() {
        return TradeRule.getItemStack(left2);
    }

    public ItemStack getSellItem() {
        return TradeRule.getItemStack(right);
    }
}
