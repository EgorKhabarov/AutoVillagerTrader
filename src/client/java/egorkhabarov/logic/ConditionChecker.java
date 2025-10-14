package egorkhabarov.logic;

import egorkhabarov.config.Condition;

public class ConditionChecker {
    public static boolean match(Condition cond, double actual) {
        if (cond == null) {
            return true;
        }
        String op = cond.condition;
        double value = Double.parseDouble(cond.value.toString());

        return switch (op) {
            case "<" -> actual < value;
            case "<=" -> actual <= value;
            case "=" -> actual == value;
            case ">" -> actual > value;
            case ">=" -> actual >= value;
            default -> false;
        };
    }
}
