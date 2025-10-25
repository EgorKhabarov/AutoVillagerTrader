package egorkhabarov.config;

public class Condition {
    public String condition; // "<", "<=", "=", ">=", ">", "in", "range", "match"
    public Object value;     // число, диапазон, список или объект

    private static String translate(String condition) {
        return switch (condition) {
            case "=" -> "eq";
            case ">" -> "mt";
            case "<" -> "lt";
            case ">=" -> "me";
            case "<=" -> "le";
            default -> "";
        };
    }

    public String getConditionId() {
        return Condition.translate(condition)+value;
    }
}
