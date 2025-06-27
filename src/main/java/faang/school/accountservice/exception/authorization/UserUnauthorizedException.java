package faang.school.accountservice.exception.authorization;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String msg) {
        super(msg);
    }
}
