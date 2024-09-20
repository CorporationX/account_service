package faang.school.accountservice.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message){super(message);}
}
