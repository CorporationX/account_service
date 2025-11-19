package faang.school.accountservice.enums;

public enum OwnerType {
    USER("Пользователь"),
    PROJECT("Проект");

    private final String description;

    OwnerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
