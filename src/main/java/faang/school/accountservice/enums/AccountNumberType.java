package faang.school.accountservice.enums;

public enum AccountNumberType {
    DEBIT(5376L),
    SAVINGS(4762L);

    public final long prefix;

    AccountNumberType(long prefix) {
        this.prefix = prefix;
    }
}
