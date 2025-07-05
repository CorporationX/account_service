package faang.school.accountservice.dto;

import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRequest {
    @NotNull
    @Positive
    private Long id;

    @NotNull
    private OwnerType type;
}
