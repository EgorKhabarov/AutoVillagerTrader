package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.TradeRule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.List;

public class VillagerTradeExecutor {
    public static void openTrade(VillagerEntity villager) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.getNetworkHandler() == null) {
            return;
        }
        VillagerCache.currentVillager = villager;
        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND);
        client.getNetworkHandler().sendPacket(packet);
    }

    public static void finishAutoTrade(int syncId, TradeOfferList offers) {
        VillagerEntity villager = VillagerCache.currentVillager;
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (
            offers == null
                || villager == null
                || !config.enabled
                || networkHandler == null
        ) {
            return;
        }
        for (int tradeIndex = 0; tradeIndex < offers.size(); tradeIndex++) {
            TradeOffer offer = offers.get(tradeIndex);
            if (offer.isDisabled()) {
                continue;
            }
            String villager_profession_id = villager.getVillagerData().profession().getIdAsString();
            List<TradeRule> tradeRules = config.professions.get(villager_profession_id);

            for (TradeRule tradeRule : tradeRules) {
                if (!tradeRule.enabled) {
                    continue;
                }

                if (tradeRule.matchOffer(offer)) {
                    networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex));
                }
            }
            break;
        }
        //networkHandler.sendPacket(new CloseHandledScreenC2SPacket(syncId));
        VillagerCache.currentVillager = null;
    }

    public static void executeTrade(ClientPlayNetworkHandler networkHandler, int syncId, int tradeIndex) {
        networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex));
        networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, 0));
    }
}
