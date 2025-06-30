package faang.school.accountservice.dto;

import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OwnerRequest {
    @NotNull
    @Positive
    private Long id;

    @NotNull
    private OwnerType type;
}
