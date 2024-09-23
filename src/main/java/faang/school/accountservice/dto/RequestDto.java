package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.StatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RequestDto {
    private UUID id;
    @NotNull
    private Long userId;
    @NotNull
    private OperationType operationType;
    @NotNull
    private Map<String, Object> requestData;
    private StatusType statusType;
    private boolean lockRequest;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
