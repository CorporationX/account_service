package faang.school.accountservice.dto.account_statement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementDtoToCreate {

    private Long accountId;
    private LocalDateTime from;
    private LocalDateTime to;
}