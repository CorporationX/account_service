package faang.school.accountservice.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone
) {
}