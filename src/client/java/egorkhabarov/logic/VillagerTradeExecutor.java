package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.TradeRule;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.List;

public class VillagerTradeExecutor {
    public static boolean openTrade(VillagerEntity villager) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.getNetworkHandler() == null) {
            return false;
        }
        try {
            PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND);
            client.getNetworkHandler().sendPacket(packet);
        } catch (Exception e) {
            return false;
        }
        VillagerCache.currentVillager = villager;
        VillagerCache.put(villager.getUuidAsString(), villager);
        System.out.println("    open " + villager.getUuidAsString() + " " + villager);
        return true;
    }

    public static void finishAutoTrade(TradeOfferList offers) {
        // System.out.println("in "+VillagerCache.currentVillager);
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        // VillagerEntity villager = VillagerCache.currentVillager;
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (
            player == null
                || offers == null
                // || villager == null
                || !config.enabled
                || networkHandler == null
        ) {
            System.out.println("player "+player);
            System.out.println("offers "+offers);
            // System.out.println("villager "+villager);
            System.out.println("config.enabled "+config.enabled);
            System.out.println("networkHandler "+networkHandler);
            return;
        }
        // System.out.println("      found " + offers.size() + " offers for villager UUID="+villager.getUuidAsString() + " " + villager);
        System.out.println("      found " + offers.size());
        for (int tradeIndex = 0; tradeIndex < offers.size(); tradeIndex++) {
            TradeOffer offer = offers.get(tradeIndex);
            if (offer.isDisabled()) {
                continue;
            }
            // String villager_profession_id = villager.getVillagerData().profession().getIdAsString();
            // List<TradeRule> tradeRules = config.professions.get(villager_profession_id);

            for (List<TradeRule> tradeRules : config.professions.values()) {
                for (TradeRule tradeRule : tradeRules) {
                    if (!tradeRule.enabled) {
                        continue;
                    }

                    if (tradeRule.matchOffer(offer)) {
                        System.out.println("        execute trade (" + player.currentScreenHandler.syncId + ", " + tradeIndex + ")");
                        // VillagerCache.skipOffer = true;
                        VillagerTradeExecutor.executeTrade(player.currentScreenHandler.syncId, tradeIndex);
                        System.out.println("        {"+offer.getDisplayedFirstBuyItem()+", "+offer.getDisplayedSecondBuyItem()+"} == "+offer.getSellItem());
                    }
                }
            }
        }
        // System.out.println("    currentScreenHandler: "+player.currentScreenHandler);
        // player.closeHandledScreen();
        // System.out.println("    close");
        // client.execute(() -> {
        //     player.closeHandledScreen();
        //     System.out.println("close");
        // });
        // player.closeHandledScreen();
        // VillagerCache.currentVillager = null;
    }

    public static void executeTrade(int syncId, int tradeIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (player == null || networkHandler == null) {
            System.out.println("player == null || networkHandler == null  "+player+" "+networkHandler);
            return;
        }

        int revision = player.currentScreenHandler.getRevision();
        short slotIndex = 2; // результат торговли
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
