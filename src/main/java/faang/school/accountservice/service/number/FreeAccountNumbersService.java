package faang.school.accountservice.service.number;

import faang.school.accountservice.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    /**
     * Предварительно сгенерировать пачку свободных номеров
     * и сохранить их в free_account_numbers.
     */
    void generateAccountNumbers(AccountType type, int batchSize);

    /**
     * Транзакционно:
     * - забрать свободный номер, если есть
     * - иначе сгенерировать новый через sequence
     * - передать номер в consumer.
     */
    void retrieveAccountNumber(AccountType type, Consumer<String> consumer);
}