package faang.school.accountservice.enums;

public enum AccountType {
    CURRENT("4200"),
    SETTLEMENT("5200"),
    CREDIT("7700"),
    DEPOSIT("1100"),
    BUDGET("3300"),
    ;
    private final String initialNumber;

    AccountType(String initialNumber) {
        this.initialNumber = initialNumber;
    }

    public String getNumber() {
        return initialNumber;
    }
}