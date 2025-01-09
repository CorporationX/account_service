package faang.school.accountservice.service.balanceaudit;

import faang.school.accountservice.dto.balanceaudit.BalanceAuditDto;
import faang.school.accountservice.mappers.balanceaudit.BalanceAuditMapperImpl;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.balanceaudit.BalanceAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceAuditServiceTest {

    @InjectMocks
    private BalanceAuditService balanceAuditService;
    @Mock
    private BalanceAuditRepository balanceAuditRepository;
    @Mock
    private BalanceAuditMapperImpl balanceAuditMapper;

    @Test
    void createBalanceAudit_shouldCreateBalanceAudit() {
        Balance balance = createBalance();
        BalanceAudit balanceAudit = createBalanceAudit();

        when(balanceAuditMapper.toBalanceAudit(balance)).thenReturn(balanceAudit);
        when(balanceAuditRepository.save(balanceAudit)).thenReturn(balanceAudit);

        BalanceAudit actualBalanceAudit = balanceAuditService.createBalanceAudit(balance);

        verify(balanceAuditRepository, times(1)).save(balanceAudit);

        assertNotNull(actualBalanceAudit);
        assertThat(balance.getAuthBalance()).isEqualTo(actualBalanceAudit.getAuthorizationBalance());
        assertThat(balance.getActualBalance()).isEqualTo(actualBalanceAudit.getCurrentBalance());

    }

    @Test
    void getBalanceAuditById() {
        BalanceAudit balanceAudit = createBalanceAudit();
        balanceAudit.setId(1L);
        BalanceAuditDto balanceAuditDto = createBalanceAuditDto();

        when(balanceAuditRepository.findById(balanceAudit.getId())).thenReturn(Optional.of(balanceAudit));
        when(balanceAuditMapper.toDto(balanceAudit)).thenReturn(balanceAuditDto);

        BalanceAuditDto actualBalanceAuditDto = balanceAuditService.getBalanceAuditById(balanceAudit.getId());

        verify(balanceAuditRepository, times(1)).findById(balanceAudit.getId());

        assertThat(balanceAudit.getId()).isEqualTo(actualBalanceAuditDto.getId());
    }

    private Balance createBalance() {
        return Balance.builder()
                .id(1L)
                .account(createAccount())
                .authBalance(BigDecimal.TEN)
                .actualBalance(BigDecimal.TEN)
                .build();
    }

    private Account createAccount() {
        return Account.builder()
                .id(1L)
                .build();
    }

    private BalanceAudit createBalanceAudit() {
        return BalanceAudit.builder()
                .id(null)
                .account(createAccount())
                .authorizationBalance(BigDecimal.TEN)
                .currentBalance(BigDecimal.TEN)
                .build();
    }

    private BalanceAuditDto createBalanceAuditDto() {
        return BalanceAuditDto.builder()
                .id(1L)
                .build();
    }
}