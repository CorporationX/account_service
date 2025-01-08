package faang.school.accountservice.entity;

public enum AccountType {

    SAVINGS(5536_0000_0000_0000L),
    DEBIT(4200_0000_0000_0000L);

    private final long accountNumber;

    AccountType(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getAccountNumber() {
        return accountNumber;
    }
}