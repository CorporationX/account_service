package faang.school.accountservice.dto.response;

import faang.school.accountservice.exception.Violation;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}
