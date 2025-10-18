package egorkhabarov.cache;

import egorkhabarov.logic.VillagerTradeExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

import java.util.LinkedList;
import java.util.Queue;

public class VillagerTradeQueue {
    public static final Queue<VillagerEntity> PENDING = new LinkedList<>();
    public static boolean busy = false;

    public static void add(VillagerEntity villager) {
        if (!PENDING.contains(villager)) {
            PENDING.add(villager);
            System.out.println("villager added in PENDING "+PENDING);
            tryStartNext();
            return;
        }
        System.out.println("PENDING already contain this villager UUID="+villager.getUuidAsString() + " " + villager);
    }

    public static void tryStartNext() {
        if (busy || PENDING.isEmpty()) {
            System.out.println("busy || PENDING.isEmpty() == "+busy+", "+PENDING.isEmpty());
            return;
        }
        VillagerEntity next = PENDING.poll();
        if (next == null) {
            markDone();
            return;
        }
        System.out.println("    try Start Next");
        busy = true;
        VillagerCache.currentVillager = next;
        // VillagerCache.autoTrading = true;
        if (!VillagerTradeExecutor.openTrade(next)) {
            markDone();
        }
        // MinecraftClient client = MinecraftClient.getInstance();
        // ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        // if (networkHandler != null) {
        //     PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.interact(next, false, Hand.MAIN_HAND);
        //     networkHandler.sendPacket(packet);
        // }
    }

    public static void markDone() {
        System.out.println("    mark Done " + PENDING);
        busy = false;
        VillagerCache.currentVillager = null;
        // VillagerCache.autoTrading = false;
        tryStartNext();
    }
}

