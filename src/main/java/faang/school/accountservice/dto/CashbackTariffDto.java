package faang.school.accountservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashbackTariffDto {

    private Long id;

    @NotNull
    @NotBlank
    @Length(min = 1, max = 256)
    private String name;

    private List<@Valid OperationTypeMappingDto> operationTypeMappingDtos;

    private List<@Valid MerchantMappingDto> merchantMappingDtos;
}
