package faang.school.accountservice.dto;

public record ApiError(String message, int status, String path, String timestamp) {
}

