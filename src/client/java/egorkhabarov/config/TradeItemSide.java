package egorkhabarov.config;

import java.util.Map;

public class TradeItemSide {
    public String item;
    public Condition count;
    public Condition price;              // если есть понятие "цены" в сделке
    public Condition nbt;                // проверки по NBT (частично или полностью)
    public Map<String, Object> nbtMatch; // конкретное NBT-содержимое (в формате JSON)
}
