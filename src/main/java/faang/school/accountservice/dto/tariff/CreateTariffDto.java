package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.Rate;
import faang.school.accountservice.model.enums.TariffType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateTariffDto {

    private TariffType type;
    private float percent;
}
