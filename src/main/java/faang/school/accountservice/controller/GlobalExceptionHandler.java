package faang.school.accountservice.controller;

import faang.school.accountservice.dto.ErrorResponse;
import faang.school.accountservice.exception.AccountNumberException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNumberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidArgumentException(AccountNumberException e) {
        String message = e.getMessage();

        return new ErrorResponse(message);
    }
}