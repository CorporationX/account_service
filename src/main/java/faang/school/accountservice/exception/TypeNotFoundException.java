package faang.school.accountservice.exception;

public class TypeNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Type %s not found!";

    public TypeNotFoundException(String type) {
        super(String.format(MESSAGE, type));
    }
}
