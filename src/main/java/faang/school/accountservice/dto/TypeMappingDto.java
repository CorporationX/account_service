package faang.school.accountservice.dto;

import faang.school.accountservice.enums.MappingType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeMappingDto {
    private Long tariffId;
    private Long typeId;
    private MappingType mappingType;
    @DecimalMin(value = "0.00", message = "Cashback percentage should be zero or more")
    @DecimalMax(value = "100.00", message = "Cashback percentage should be 100 or less")
    private Double cashbackPercentage;
}
