package faang.school.accountservice.exception.transfer;

import faang.school.accountservice.exception.transfer.kafka.AccountDifferentCurrencyException;
import faang.school.accountservice.exception.transfer.kafka.AccountNotAvailableException;
import faang.school.accountservice.exception.transfer.kafka.AccountNotExistException;
import faang.school.accountservice.exception.transfer.kafka.NotAllowedOperationException;
import faang.school.accountservice.exception.transfer.kafka.NotEnoughActualFundsException;
import faang.school.accountservice.exception.transfer.kafka.NotEnoughAuthorizedFundsException;
import faang.school.accountservice.exception.transfer.kafka.TransferFinishedException;
import faang.school.accountservice.exception.transfer.kafka.WrongStageException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Locale;
import java.util.Map;

import static faang.school.accountservice.util.MessageSourceHolder.STATIC_MESSAGE_SOURCE;

public class TransferException extends RuntimeException {

    private static final String ERROR_MESSAGE_PREFIX = "transfer.error.kafka.%s";

    private static final Map<Class<?>, String> MESSAGE_STORAGE;
    private final String kafkaMessageCode;

    static {
        MESSAGE_STORAGE = Map.of(
                OptimisticLockException.class, ERROR_MESSAGE_PREFIX.formatted("optimistic_lock"),
                OptimisticLockingFailureException.class, ERROR_MESSAGE_PREFIX.formatted("optimistic_lock"),
                AccountDifferentCurrencyException.class, ERROR_MESSAGE_PREFIX.formatted("different_account_currency"),
                AccountNotAvailableException.class, ERROR_MESSAGE_PREFIX.formatted("account_not_available"),
                AccountNotExistException.class, ERROR_MESSAGE_PREFIX.formatted("account_not_exist"),
                NotAllowedOperationException.class, ERROR_MESSAGE_PREFIX.formatted("not_allowed"),
                NotEnoughActualFundsException.class, ERROR_MESSAGE_PREFIX.formatted("low_actual_funds"),
                NotEnoughAuthorizedFundsException.class, ERROR_MESSAGE_PREFIX.formatted("low_authorized_funds"),
                TransferFinishedException.class, ERROR_MESSAGE_PREFIX.formatted("transfer_finished"),
                WrongStageException.class, ERROR_MESSAGE_PREFIX.formatted("wrong_stage")
        );
    }

    public <T extends Exception> TransferException(T exception) {
        super(exception.getMessage());
        this.kafkaMessageCode = MESSAGE_STORAGE.getOrDefault(exception.getClass(), ERROR_MESSAGE_PREFIX.formatted("default_error"));
    }

    public String getKafkaMessage() {
        return STATIC_MESSAGE_SOURCE.getMessage(kafkaMessageCode, null, Locale.getDefault());
    }
}