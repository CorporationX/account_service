package faang.school.accountservice.dto.dms;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRequestDto {
    private String operationId;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private String token;
    private RequestType requestType;
    private Map<String, String> requestInputData;
    private String addictionalDetail;
}