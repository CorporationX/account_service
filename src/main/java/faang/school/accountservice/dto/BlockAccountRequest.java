package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BlockAccountRequest(
        @JsonProperty(value = "reason")
        String reason
) {
}
