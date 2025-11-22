package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.model.OwnerType;

public record OwnerDto(
        @JsonProperty("type")
        OwnerType ownerType,
        @JsonProperty("person_id")
        Long personId
) {
}
