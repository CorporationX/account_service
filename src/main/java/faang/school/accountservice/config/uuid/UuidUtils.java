package faang.school.accountservice.config.uuid;

import java.util.UUID;

public class UuidUtils {

    public static Long uuidToLong(UUID uuid) {
        return uuid.getMostSignificantBits();
    }
}