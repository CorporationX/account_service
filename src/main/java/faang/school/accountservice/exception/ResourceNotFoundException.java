package faang.school.accountservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceNotFoundException extends ApiException {
    private static final String MESSAGE = "%s id=%s not found";

    public ResourceNotFoundException(Class<?> resource, UUID id) {
        super(MESSAGE, HttpStatus.NOT_FOUND, resource.getName(), id);
    }
}
