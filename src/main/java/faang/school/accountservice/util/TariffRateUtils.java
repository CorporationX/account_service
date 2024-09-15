package faang.school.accountservice.util;

public class TariffRateUtils {

    public static String formatHistory(String history, String newValue) {
        if (history == null || history.isBlank()) {
            return "[" + newValue + "]";
        }
        return history.replaceAll("]", "," + newValue + "]");
    }

    public static String getLastValueFromHistory(String history) {
        if (history.contains(",")) {
            String[] values = history.split(",");
            int lastIndex = values.length - 1;
            return values[lastIndex].substring(0, values[lastIndex].length() - 1);
        } else {
            return history.substring(1, history.length() - 1);
        }
    }
}
