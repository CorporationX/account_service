package faang.school.accountservice.enums;

import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import faang.school.accountservice.exception.UnsupportedAccountTypeException;
import lombok.Getter;
import lombok.NonNull;

@Getter
public enum AccountType {
    DEBIT("4200"),
    SAVINGS("5236"),
    CREDIT("5500"),
    BUSINESS("6100"),
    CURRENT_INDIVIDUAL(null),
    CURRENT_CORPORATE(null),
    CURRENCY(null),
    CRYPTO(null);

    private final String prefix;

    AccountType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Генерация номера счёта. Работает только для типов с префиксом.
     */
    public String generateAccountNumber(long sequence) {
        requirePrefix();
        if (sequence <= 0) throw new AccountNumberGenerationException("Sequence must be positive: " + sequence);

        String accountNumber = prefix + String.format("%08d", sequence);

        if (accountNumber.length() > 20)
            throw new AccountNumberGenerationException("Generated account number exceeds 20 characters: " + accountNumber);

        return accountNumber;
    }

    /**
     * Проверка валидности номера счёта.
     */
    public boolean isValidAccountNumber(@NonNull String accountNumber) {
        if (prefix == null) return false;
        if (!accountNumber.matches("\\d+")) return false;
        if (!accountNumber.startsWith(prefix)) return false;

        String numberPart = accountNumber.substring(prefix.length());
        if (numberPart.length() < 8 || numberPart.length() + prefix.length() > 20) return false;

        try {
            return Long.parseLong(numberPart) > 0;
        } catch (NumberFormatException e) {
            throw new InvalidAccountNumberException("Account number contains invalid sequence part: " + accountNumber, e);
        }
    }

    /**
     * Извлечение последовательного номера.
     */
    public Long extractSequence(@NonNull String accountNumber) {
        if (!isValidAccountNumber(accountNumber)) return null;
        return Long.parseLong(accountNumber.substring(prefix.length()));
    }

    /**
     * Проверка корректного префикса.
     */
    public boolean hasCorrectPrefix(@NonNull String accountNumber) {
        return prefix != null && accountNumber.startsWith(prefix);
    }

    /**
     * Максимальный допустимый номер последовательности.
     */
    public long getMaxSequenceValue() {
        if (prefix == null) return 0;
        return (long) Math.pow(10, 20 - prefix.length()) - 1;
    }

    /**
     * Проверка допустимости последовательного номера.
     */
    public boolean isSequenceValid(long sequence) {
        return prefix != null && sequence > 0 && sequence <= getMaxSequenceValue();
    }

    /**
     * Вспомогательный метод: выбрасывает исключение, если префикс отсутствует.
     */
    private void requirePrefix() {
        if (prefix == null) {
            throw new UnsupportedAccountTypeException("Account type " + this + " does not support account numbers.");
        }
    }
}
