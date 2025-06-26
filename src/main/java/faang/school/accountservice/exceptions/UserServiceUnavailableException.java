package faang.school.accountservice.exceptions;

public class UserServiceUnavailableException extends DataValidationException {
    public UserServiceUnavailableException(String message) {
        super(message);
    }
}
