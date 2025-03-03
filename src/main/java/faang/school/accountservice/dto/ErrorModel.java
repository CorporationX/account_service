package faang.school.accountservice.dto;

public record ErrorModel(
        String message,
        int statusCode,
        String serviceName) {
}