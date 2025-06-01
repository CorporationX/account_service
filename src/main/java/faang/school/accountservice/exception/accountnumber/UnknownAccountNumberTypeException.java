package faang.school.accountservice.exception.accountnumber;

public class UnknownAccountNumberTypeException extends RuntimeException {

    public UnknownAccountNumberTypeException(String message) {
        super(message);
    }
}
