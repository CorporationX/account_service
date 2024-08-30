package faang.school.accountservice.exeption;

public class DataValidationException extends RuntimeException {

    public DataValidationException(ErrorMessage messageError) {
        super(messageError.getMessage());
    }

    public DataValidationException(String message) {
        super(message);
    }
}
