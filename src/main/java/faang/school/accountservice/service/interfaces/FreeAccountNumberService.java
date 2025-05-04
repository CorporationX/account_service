package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.function.Consumer;

@Validated
public interface FreeAccountNumberService {
    void generateOneAccountNumber(@NotNull AccountType type);

    void useFreeAccountNumber(@NotNull AccountType type, Consumer<Long> action);

    void generateAccountNumbers(@NotNull AccountType type, int batchSize);

    String generateAccountNumber(AccountType accountType);
}
