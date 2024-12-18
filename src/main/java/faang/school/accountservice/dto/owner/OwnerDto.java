package faang.school.accountservice.dto.owner;

import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {
    @NotNull
    @Positive
    private Long ownerId;

    @NotNull
    private OwnerType ownerType;
}
