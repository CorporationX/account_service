package faang.school.accountservice.controller;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ENTITY_NOT_FOUND("ENTITY NOT FOUND", HttpStatus.NOT_FOUND),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus code;

    ErrorCode(String message, HttpStatus code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getCode() {
        return code;
    }
}
