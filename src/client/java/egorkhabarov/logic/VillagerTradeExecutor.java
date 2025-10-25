package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.TradeRule;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class VillagerTradeExecutor {
    public static void finishAutoTrade(TradeOfferList offers) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (
            player == null
                || offers == null
                || !config.enabled
                || networkHandler == null
        ) {
            return;
        }
        System.out.println("      found " + offers.size());
        for (int tradeIndex = 0; tradeIndex < offers.size(); tradeIndex++) {
            TradeOffer offer = offers.get(tradeIndex);
            if (offer.isDisabled()) {
                continue;
            }

            for (TradeRule tradeRule : config.trades) {
                if (!tradeRule.enabled) {
                    continue;
                }

                if (tradeRule.matchOffer(offer)) {
                    System.out.println("        execute trade (" + player.currentScreenHandler.syncId + ", " + tradeIndex + ")");
                    VillagerTradeExecutor.executeTrade(player.currentScreenHandler.syncId, tradeIndex);
                    System.out.println("        {"+offer.getDisplayedFirstBuyItem()+", "+offer.getDisplayedSecondBuyItem()+"} == "+offer.getSellItem());
                }
            }
        }
    }

    public static void executeTrade(int syncId, int tradeIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (player == null || networkHandler == null) {
            return;
        }

        int revision = player.currentScreenHandler.getRevision();
        short slotIndex = 2;
        byte button = 0;
        networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex));
        networkHandler.sendPacket(new ClickSlotC2SPacket(
            syncId,
            revision,
            slotIndex,
            button,
            SlotActionType.QUICK_MOVE, // Shift+Click
            new Int2ObjectOpenHashMap<>(),
            ItemStackHash.EMPTY
        ));
        System.out.println("          trade");
    }
}
