package faang.school.accountservice.dto.project;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDto {
    private long id;
    private String name;
    private String description;
    private long ownerId;
    private long parentProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
