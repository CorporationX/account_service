package faang.school.accountservice.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {

    private String message;
    private LocalDateTime timestamp;

    public ErrorMessage(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}

