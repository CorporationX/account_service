package faang.school.accountservice.exeption;

public class NotFoundException extends RuntimeException {

    public NotFoundException(ErrorMessage messageError) {
        super(messageError.getMessage());
    }

    public NotFoundException(String message) {
        super(message);
    }
}
