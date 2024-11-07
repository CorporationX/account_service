package faang.school.accountservice.dto.project;

import java.util.List;

public record TeamDto(Long id, List<TeamMemberDto> teamMembers) {}
