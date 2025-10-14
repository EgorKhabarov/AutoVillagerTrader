package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.config.TradeRule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import egorkhabarov.config.ConfigData;

import java.util.List;

public class VillagerFinder {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void tick() {
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        if (client.world == null || client.player == null || !config.enabled) {
            return;
        }
        List<VillagerEntity> villagers = client.world.getEntitiesByClass(
            VillagerEntity.class,
            client.player.getBoundingBox().expand(2),
            v -> client.player.canSee(v)
                && config.professions.containsKey(v.getVillagerData().profession().getIdAsString())
        );

        for (VillagerEntity villager : villagers) {
            String villager_profession_id = villager.getVillagerData().profession().getIdAsString();

            List<TradeRule> tradeRules = config.professions.get(villager_profession_id);
            if (tradeRules == null) {
                continue;
            }
            VillagerTradeExecutor.openTrade(villager);
        }
    }
}
