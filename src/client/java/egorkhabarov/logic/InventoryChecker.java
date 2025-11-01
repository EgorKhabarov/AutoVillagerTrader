package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.TradeRule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class InventoryChecker {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static @NotNull PlayerInventory getInventory() {
        assert client.player != null;
        return client.player.getInventory();
    }

    public static boolean canTrade() {
        int canPerformTrade = 0;
        for (TradeRule recipe : AutoVillagerTraderModClient.CONFIG.trades) {
            if (!recipe.enabled) {
                continue;
            }
            ItemStack left = recipe.getFirstBuyItem();
            ItemStack left2 = recipe.getSecondBuyItem();
            ItemStack right = recipe.getSellItem();
            if (
                left.getItem() != Items.AIR
                && !InventoryChecker.hasEnoughItemsInInventory(left)
            ) {
                continue;
            }
            if (
                left2.getItem() != Items.AIR
                && !InventoryChecker.hasEnoughItemsInInventory(left2)
            ) {
                continue;
            }
            if (
                right.getItem() != Items.AIR
                && !InventoryChecker.canReceiveOutput(right)
            ) {
                continue;
            }
            canPerformTrade += 1;
        }
        return canPerformTrade != 0;
    }

    private static boolean hasEnoughItemsInInventory(ItemStack stack) {
        int remaining = stack.getCount();

        for (int i = 0; i < 36; i++) {
            ItemStack inventoryStack = InventoryChecker.getInventory().getStack(i);

            if (inventoryStack == null) {
                continue;
            }
            if (InventoryChecker.areItemStacksMergable(stack, inventoryStack)) {
                remaining -= inventoryStack.getCount();
            }
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean canReceiveOutput(ItemStack stack) {
        int remaining = stack.getCount();

        for (int i = 0; i < 36; i++) {
            ItemStack inventoryStack = InventoryChecker.getInventory().getStack(i);
            if (inventoryStack == null || inventoryStack.isEmpty()) {
                return true;
            }
            if (
                InventoryChecker.areItemStacksMergable(stack, inventoryStack)
                &&  stack.getMaxCount() >= stack.getCount() + inventoryStack.getCount()
            ) {
                remaining -= inventoryStack.getMaxCount() - inventoryStack.getCount();
            }
            if (remaining <= 0)
                return true;
        }
        return false;
    }

    private static boolean areItemStacksMergable(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        }
        return a.getItem() == b.getItem()
            && (!a.isDamageable() || a.getDamage() == b.getDamage())
            && ItemStack.areItemsAndComponentsEqual(a, b);
    }

    public static Integer getCount() {
        int i = 0;
        Set<Item> items = new HashSet<>();
        for (TradeRule recipe : AutoVillagerTraderModClient.CONFIG.trades) {
            if (!recipe.enabled) {
                continue;
            }
            items.add(recipe.getFirstBuyItem().getItem());
            items.add(recipe.getSecondBuyItem().getItem());
        }
        items.remove(Items.AIR);
        for (Item item : items) {
            i += InventoryChecker.getInventory().count(item);
        }
        return i;
    }
}
