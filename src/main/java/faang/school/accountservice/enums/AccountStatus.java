package faang.school.accountservice.enums;

public enum AccountStatus {
    ACTIVE("Действующий"),
    FROZEN("Замороженный"),
    BLOCKED("Заблокированный"),
    CLOSED("Закрытый");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
