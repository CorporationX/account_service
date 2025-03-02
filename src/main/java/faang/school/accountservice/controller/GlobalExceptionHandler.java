package faang.school.accountservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalStateExceptions(IllegalStateException e) {
        log.error("Illegal resource state exception {}\n{}", e.getMessage(), e.getStackTrace());
        return e.getMessage();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSqlConstraintsException(DataIntegrityViolationException e) {
        log.error("Sql constraints validation exception {}\n{}", e.getMessage(), e.getStackTrace());
        return e.getMessage();
    }
}
