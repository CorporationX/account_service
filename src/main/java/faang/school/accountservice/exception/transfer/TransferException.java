package faang.school.accountservice.exception.transfer;

import java.util.Locale;
import java.util.Map;

import static faang.school.accountservice.util.MessageSourceHolder.STATIC_MESSAGE_SOURCE;

public class TransferException extends RuntimeException {

    private static final Map<Class<?>, String> MESSAGE_STORAGE;
    private final String kafkaMessageCode;

    static {
        MESSAGE_STORAGE = Map.of();
    }

    public <T extends Exception> TransferException(T exception) {
        super(exception.getMessage());
        this.kafkaMessageCode = MESSAGE_STORAGE.getOrDefault(exception.getClass(), "transfer.default_error");
    }

    public String getKafkaMessage() {
        return STATIC_MESSAGE_SOURCE.getMessage(kafkaMessageCode, null, Locale.getDefault());
    }
}