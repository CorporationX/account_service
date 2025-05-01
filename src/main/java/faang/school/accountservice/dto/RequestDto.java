package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Long id;
    @NotBlank
    private RequestType requestType;
    private boolean isOpen;
    private Long userId;
    @NotBlank
    private Map<String, Object> inputData;
    private RequestStatus status;
    private String statusDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
