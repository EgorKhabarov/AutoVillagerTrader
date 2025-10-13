package egorkhabarov.config;

public class Condition {
    public String condition; // "<", "<=", "=", ">=", ">", "in", "range", "match"
    public Object value;     // число, диапазон, список или объект
}
