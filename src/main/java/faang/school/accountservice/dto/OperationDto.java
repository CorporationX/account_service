package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OperationDto {
    private Long id;
    @NotNull
    private Long accountId;
    @Positive
    private double sum;
    @NotBlank
    private Operation operation;
}
