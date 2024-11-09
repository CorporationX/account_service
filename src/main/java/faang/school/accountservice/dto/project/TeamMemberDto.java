package faang.school.accountservice.dto.project;

import faang.school.accountservice.enums.project.TeamRole;

import java.util.Set;

public record TeamMemberDto(
        long id,
        long userId,
        Set<TeamRole> roles) {}