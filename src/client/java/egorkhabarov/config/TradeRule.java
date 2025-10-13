package egorkhabarov.config;

public class TradeRule {
    public boolean enabled = true;
    public long cooldown_ms = 0;
    public String comment;
    public TradeItemSide left;   // то, что игрок отдаёт
    public TradeItemSide left2;  // если у сделки две вещи слева (опционально)
    public TradeItemSide right;  // то, что игрок получает
}
