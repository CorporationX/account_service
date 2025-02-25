package faang.school.accountservice.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountReadDto {
    private long id;
    private String invoice;
    private Long authorId;
    private Long projectId;
    private InvoiceType invoiceType;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
