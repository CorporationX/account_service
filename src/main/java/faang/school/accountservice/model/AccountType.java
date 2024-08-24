package faang.school.accountservice.model;

public enum AccountType {
    SETTLEMENT_ACCOUNT("Settlement accounts"),
    CURRENCY_ACCOUNT("currency account"),
    BUDGET_ACCOUNT("budget account"),
    CREDIT_ACCOUNT("credit_account");
    private final String accounts;

    AccountType(String type) {
        this.accounts = type;
    }

    public String getMessage() {
        return accounts;
    }
}