package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestCreateDto {

    @NotNull
    private Long userId;

    @NotNull
    private RequestType requestType;

    private Map<String, Object> inputData;

    private String description;
}
