package faang.school.accountservice.exception;

public class IllegalAccountStatus extends RuntimeException {
    public IllegalAccountStatus(String message) {
        super(message);
    }
}
