package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import egorkhabarov.cache.VillagerTradeQueue;
import egorkhabarov.config.TradeRule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import egorkhabarov.config.ConfigData;

import java.util.List;

public class VillagerFinder {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void tick() {
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        if (client.world == null || client.player == null || !config.enabled || !config.auto_finder_enabled) {
            return;
        }
        // if (!VillagerTradeQueue.PENDING.isEmpty()) {
        //     System.out.println("VillagerTradeQueue.PENDING = "+VillagerTradeQueue.PENDING);
        //     return;
        // }
        List<VillagerEntity> villagers = client.world.getEntitiesByClass(
            VillagerEntity.class,
            client.player.getBoundingBox().expand(config.scan_radius),
            v -> {
                if (config.need_see && !client.player.canSee(v)) {
                    return false;
                }
                return config.professions.containsKey(v.getVillagerData().profession().getIdAsString());
            }
        );

        // if (!villagers.isEmpty()) {
        //     System.out.println("for (VillagerEntity villager : villagers{" + villagers.size() + "})");
        // }
        for (VillagerEntity villager : villagers) {
            if (VillagerCache.get(villager) != null) {
                continue;
            }
            String villager_profession_id = villager.getVillagerData().profession().getIdAsString();
            List<TradeRule> tradeRules = config.professions.get(villager_profession_id);
            if (tradeRules == null) {
                continue;
            }
            System.out.println("  villager: UUID="+villager.getUuidAsString() + " " + villager);
            // VillagerTradeQueue.add(villager);
            VillagerTradeExecutor.openTrade(villager);
            break;
        }
        // if (!villagers.isEmpty()) {
        //     System.out.println("} size("+VillagerTradeQueue.PENDING.size()+") {"+VillagerTradeQueue.PENDING+"}");
        // }
    }
}
