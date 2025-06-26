package faang.school.accountservice.exceptions;

public class UserNotFoundException extends DataValidationException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
