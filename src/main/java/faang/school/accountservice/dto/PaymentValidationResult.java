package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccValidationStatus;

public record PaymentValidationResult(AccValidationStatus status, Exception error) {
}