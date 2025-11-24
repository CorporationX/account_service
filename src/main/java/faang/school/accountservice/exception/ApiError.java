package faang.school.accountservice.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiError {
    private int status;
    private String code;
    private String message;
}