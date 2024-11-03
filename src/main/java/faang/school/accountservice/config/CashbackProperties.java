package faang.school.accountservice.config;

import faang.school.accountservice.enums.OperationType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "cashback.tariff")
public class CashbackProperties {
    private List<OperationType> expenditureOperations;
}

