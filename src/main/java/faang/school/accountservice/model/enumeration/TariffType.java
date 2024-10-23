package faang.school.accountservice.model.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum TariffType {
    BASE,
    PROMO,
    FOR_SUBSCRIPTION;

    private static final Map<String, TariffType> NAME_TO_ENUM = new HashMap<>();

    static {
        for (TariffType type : TariffType.values()) {
            NAME_TO_ENUM.put(type.name(), type);
        }
    }

    public static TariffType getByName(String name) {
        return NAME_TO_ENUM.get(name);
    }
}
