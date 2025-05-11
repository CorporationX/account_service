package faang.school.accountservice.exception;

public class DataConversionException extends RuntimeException {
    public DataConversionException(String message, Exception e) {
        super(message, e);
    }
}
