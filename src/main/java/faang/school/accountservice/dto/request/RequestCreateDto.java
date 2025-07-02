package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.request.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateDto {
    @NotNull
    private Long userId;

    @NotNull
    private RequestType requestType;

    private Map<String, Object> storage;

    private String details;
}