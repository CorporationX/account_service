package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BalanceAuditMapperTest {

    private BalanceAuditMapper balanceAuditMapper;

    private Balance balance;
    private BalanceAudit balanceAudit;

    @BeforeEach
    void setUp() {
        balanceAuditMapper = Mappers.getMapper(BalanceAuditMapper.class);
        balance = Balance.builder()
                .id(1L)
                .account(Account.builder()
                        .number("123456789012")
                        .build())
                .currentBalance(BigDecimal.TEN)
                .authorizedBalance(BigDecimal.valueOf(100))
                .version(1L)
                .build();
    }

    @Test
    void shouldMapBalanceToBalanceAuditWithOperationId() {
        Long operationId = 123L;
        balanceAudit = balanceAuditMapper.fromBalance(balance, operationId);
        assertThat(balanceAudit.getAccountNumber()).isEqualTo(123456789012L);
        assertThat(balanceAudit.getCurrentAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(balanceAudit.getAuthorizedAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(balanceAudit.getOperationId()).isEqualTo(operationId);
        assertThat(balanceAudit.getBalanceVersion()).isEqualTo(1L);
    }
}