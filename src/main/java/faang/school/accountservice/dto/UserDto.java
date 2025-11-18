package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
        Long id,
        String username,
        String email,
        Boolean active
) {
}
