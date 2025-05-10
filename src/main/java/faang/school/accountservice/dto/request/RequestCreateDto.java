package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestCreateDto {

    @NotNull
    private Long userId;

    @NotNull
    private RequestType requestType;

    @NotNull
    private Map<String, Object> inputData;

    private String description;

}
