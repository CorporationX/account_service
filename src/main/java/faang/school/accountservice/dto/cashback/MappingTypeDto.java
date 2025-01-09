package faang.school.accountservice.dto.cashback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingTypeDto {
    private Long id;
    private String name;
}