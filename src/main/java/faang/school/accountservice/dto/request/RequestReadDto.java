package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestReadDto {
    private TransactionType transactionType;
    private String authorAccountNumber;
    private String receiverAccountNumber;
    private RequestStatus requestStatus;
}
