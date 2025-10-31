package egorkhabarov.config;

import java.util.List;

public class ConfigData {
    public boolean enabled = false;
    public boolean auto_finder_enabled = false;
    public long scan_interval_ms = 1000;
    public double scan_radius = 0.5;
    public long villager_cache_ttl = 10000;
    public boolean need_see = true;
    public List<TradeRule> trades;
    public List<String> professions;
}
