package egorkhabarov.config;

import java.util.Map;
import java.util.List;

public class ConfigData {
    public boolean enabled = true;
    public String comment;
    public Map<String, List<TradeRule>> professions;
    public long scan_interval_ms = 1000;
    public double scan_radius = 2.0;
}
