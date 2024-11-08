package faang.school.accountservice.dto.cashback;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCashbackTariffDto {
    @NotNull(message = "Name can't be blank or empty")
    @Length(max = 255, message = "Maximum number of characters 255 chairs")
    private String name;
}
