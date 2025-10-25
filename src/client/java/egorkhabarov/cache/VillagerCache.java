package egorkhabarov.cache;

import egorkhabarov.AutoVillagerTraderModClient;
import net.minecraft.entity.passive.VillagerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VillagerCache {
    public static VillagerEntity currentVillager;

    private static final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final int MAX_SIZE = 50;

    public static void put(String key, VillagerEntity villager) {
        if (cache.size() >= MAX_SIZE) {
            cache.keySet().stream().findAny().ifPresent(cache::remove);
        }
        cache.put(key, new CacheEntry(villager, System.currentTimeMillis()));
    }

    public static VillagerEntity get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        long TTL_MILLIS = AutoVillagerTraderModClient.CONFIG.villager_cache_ttl;
        if (System.currentTimeMillis() - entry.timestamp > TTL_MILLIS) {
            cache.remove(key);
            return null;
        }
        return entry.villager;
    }

    public static void put(VillagerEntity villager) {
        VillagerCache.put(villager.getUuidAsString(), villager);
    }

    public static VillagerEntity get(VillagerEntity villager) {
        return VillagerCache.get(villager.getUuidAsString());
    }

    public static void cleanup() {
        long now = System.currentTimeMillis();
        long TTL_MILLIS = AutoVillagerTraderModClient.CONFIG.villager_cache_ttl;
        for (Map.Entry<String, CacheEntry> e : cache.entrySet()) {
            if (now - e.getValue().timestamp > TTL_MILLIS) {
                cache.remove(e.getKey());
            }
        }
    }

    public static void clear() {
        cache.clear();
    }

    private record CacheEntry(VillagerEntity villager, long timestamp) {}
}
