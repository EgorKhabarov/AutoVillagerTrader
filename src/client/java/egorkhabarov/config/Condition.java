package egorkhabarov.config;

import java.util.Objects;

public class Condition {
    public String condition; // "<", "<=", "=", ">=", ">", "in", "range", "match"
    public Integer value;     // число, диапазон, список или объект

    public Condition(String condition, Integer value) {
        this.condition = condition;
        this.value = value;
        if (Objects.equals(this.condition, "<") && this.value == 0) {
            this.condition = "=";
            this.value = 1;
        }
        if (Objects.equals(this.condition, "<=") && this.value == 0) {
            this.condition = "=";
            this.value = 1;
        }
        if (Objects.equals(this.condition, ">=") && this.value == 0) {
            this.condition = ">=";
            this.value = 1;
        }
        if (Objects.equals(this.condition, ">") && this.value == 0) {
            this.condition = ">=";
            this.value = 1;
        }
    }

    private static String translate(String condition) {
        return switch (condition) {
            case "=", "==" -> "eq";
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
