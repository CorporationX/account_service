package faang.school.accountservice.enums.account;

public enum AccountEnum {
    DEBIT("4200"),
    SAVINGS("5236");

    private final String prefix;

    AccountEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}