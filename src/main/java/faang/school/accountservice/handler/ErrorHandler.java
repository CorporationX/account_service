package faang.school.accountservice.handler;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
