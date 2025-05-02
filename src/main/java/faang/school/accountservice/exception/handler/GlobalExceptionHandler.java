package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.OpenRequestExistsException;
import faang.school.accountservice.exception.RequestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OpenRequestExistsException.class)
    public ResponseEntity<ErrorResponse> handleOpenRequestExistsException(OpenRequestExistsException e) {
        String message = "We cannot process your request because you've already opened another one. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(e, HttpStatus.CONFLICT, e.getMessage())
                .title("Open Request Conflict")
                .detail(message)
                .property("Service", "Request")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFoundException(RequestNotFoundException e) {
        String message = "We cannot found your request.";
        ErrorResponse errorResponse = ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage())
                .title("Request not found")
                .detail(message)
                .property("Service", "Request")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
