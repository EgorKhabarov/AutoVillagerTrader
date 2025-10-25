package egorkhabarov.logic;

import egorkhabarov.AutoVillagerTraderModClient;
import egorkhabarov.cache.VillagerCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import egorkhabarov.config.ConfigData;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

import java.util.HashSet;
import java.util.List;

public class VillagerFinder {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void tick() {
        ConfigData config = AutoVillagerTraderModClient.CONFIG;
        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (
            client.world == null
                || player == null
                || networkHandler == null
                || !config.enabled
                || !config.auto_finder_enabled
                || config.trades == null
        ) {
            return;
        }
        HashSet<String> professions = config.getProfessions();
        List<VillagerEntity> villagers = client.world.getEntitiesByClass(
            VillagerEntity.class,
            player.getBoundingBox().expand(config.scan_radius),
            v -> {
                if (config.need_see && !player.canSee(v)) {
                    return false;
                }
                return professions.contains(v.getVillagerData().profession().getIdAsString());
            }
        );

        for (VillagerEntity villager : villagers) {
            if (VillagerCache.get(villager) != null) {
                continue;
            }
            System.out.println("  villager: UUID="+villager.getUuidAsString() + " " + villager);

            try {
                PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND);
                networkHandler.sendPacket(packet);
            } catch (Exception e) {
                break;
            }
            VillagerCache.put(villager);
            break;
        }
    }
}
