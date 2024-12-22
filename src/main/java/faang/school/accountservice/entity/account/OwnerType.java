package faang.school.accountservice.entity.account;

public enum OwnerType {
    USER,
    PROJECT;

    public static OwnerType getOwnerById(long ownerId) {
        for (OwnerType ownerType : OwnerType.values()) {
            if (ownerType.ordinal() == ownerId) {
                return ownerType;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid ownerId: %d", ownerId));
    }

}
