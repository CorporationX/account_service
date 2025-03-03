package faang.school.accountservice.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectReadDto {
    private long id;
    private String name;
    private long ownerId;
    private long parentProjectId;
    private ProjectStatus projectStatus;
    private ProjectVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
