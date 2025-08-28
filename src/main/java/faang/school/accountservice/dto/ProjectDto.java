package faang.school.accountservice.dto;

import faang.school.accountservice.enums.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectDto(
        Long id,
        String name,
        Long ownerId,
        ProjectStatus status
) {
}
