package egorkhabarov.config;

import java.util.List;
import java.util.HashSet;

public class ConfigData {
    public boolean enabled = false;
    public boolean auto_finder_enabled = false;
    public long scan_interval_ms = 1000;
    public double scan_radius = 2.0;
    public long villager_cache_ttl = 5000;
    public boolean need_see = true;
    public List<TradeRule> trades;

    public HashSet<String> getProfessions() {
        HashSet<String> professions = new HashSet<>();
        for (TradeRule trade : this.trades) {
            if (trade.enabled) {
                professions.addAll(trade.professions);
            }
        }
        return professions;
    }
}
