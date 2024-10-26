package faang.school.accountservice.exception;

public class IllegalAccountTypeForOwner extends RuntimeException {
    public IllegalAccountTypeForOwner(String message) {
        super(message);
    }
}
