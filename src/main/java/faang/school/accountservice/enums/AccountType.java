package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    DEBIT("4200"),
    SAVINGS("5236"),
    CREDIT("5500"),
    BUSINESS("6100");

    private final String prefix;

    AccountType(String prefix) {
        this.prefix = prefix;
    }

    public String generateAccountNumber(long sequence) {
        if (sequence <= 0) {
            throw new IllegalArgumentException("Sequence must be positive: " + sequence);
        }

        String sequenceStr = String.valueOf(sequence);

        if (sequenceStr.length() < 8) {
            sequenceStr = String.format("%08d", sequence);
        }

        String accountNumber = prefix + sequenceStr;

        if (accountNumber.length() > 20) {
            throw new IllegalStateException(
                    "Generated account number exceeds maximum length of 20 characters: "
                            + accountNumber
            );
        }

        return accountNumber;
    }

    public boolean isValidAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return false;
        }

        if (accountNumber.length() < 12 || accountNumber.length() > 20) {
            return false;
        }

        if (!accountNumber.matches("\\d+")) {
            return false;
        }

        if (!accountNumber.startsWith(prefix)) {
            return false;
        }

        String numberPart = accountNumber.substring(prefix.length());
        if (numberPart.length() < 8) {
            return false;
        }

        try {
            long sequenceValue = Long.parseLong(numberPart);
            return sequenceValue > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public Long extractSequence(String accountNumber) {
        if (!isValidAccountNumber(accountNumber)) {
            return null;
        }

        String numberPart = accountNumber.substring(prefix.length());
        try {
            return Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean hasCorrectPrefix(String accountNumber) {
        return accountNumber != null && accountNumber.startsWith(prefix);
    }

    public long getMaxSequenceValue() {
        int maxSequenceDigits = 20 - prefix.length();
        return (long) Math.pow(10, maxSequenceDigits) - 1;
    }

    public boolean isSequenceValid(long sequence) {
        return sequence > 0 && sequence <= getMaxSequenceValue();
    }
}