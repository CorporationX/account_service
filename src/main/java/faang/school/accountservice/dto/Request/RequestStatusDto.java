package faang.school.accountservice.dto.Request;

import faang.school.accountservice.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusDto {
    @NotNull
    private RequestStatus status;
    @NotBlank
    private String statusDetails;
}
