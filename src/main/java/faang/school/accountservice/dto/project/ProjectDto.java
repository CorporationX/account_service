package faang.school.accountservice.dto.project;

import java.util.List;

public record ProjectDto(Long id, List<TeamDto> teams) {}
