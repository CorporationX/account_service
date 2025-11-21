package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.model.OwnerPerson;

public record OwnerDto(
        @JsonProperty("type")
        OwnerPerson ownerPerson,
        @JsonProperty("person_id")
        Long personId
) {
}
