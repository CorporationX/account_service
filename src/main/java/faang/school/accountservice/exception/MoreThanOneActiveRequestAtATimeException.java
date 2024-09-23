package faang.school.accountservice.exception;

public class MoreThanOneActiveRequestAtATimeException extends RuntimeException{
    public MoreThanOneActiveRequestAtATimeException(String message){super(message);}
}
