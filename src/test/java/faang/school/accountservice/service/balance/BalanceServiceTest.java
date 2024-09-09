package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.validator.balance.BalanceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceValidator balanceValidator;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BalanceService balanceService;

    private BalanceDto balanceDto;
    private Balance balance;
    private Account account;

    @BeforeEach
    void setUp() {
        balance = Balance.builder()
                .id(1L)
                .actualBalance(BigDecimal.valueOf(111.11))
                .build();

        balanceDto = BalanceDto.builder()
                .id(balance.getId())
                .actualBalance(balance.getActualBalance())
                .build();

        account = Account.builder()
                .id(UUID.fromString("8f422a3d-6a1c-4181-8505-07e34b190d02"))
                .build();
    }

    @Test
    void createTest() {
        Mockito.when(balanceMapper.toEntity(balanceDto)).thenReturn(balance);
        Mockito.when(accountRepository.findById(balanceDto.getAccountId())).thenReturn(Optional.of(account));

        balanceService.create(balanceDto);

        Mockito.verify(balanceRepository).save(balance);
    }

    @Test
    void updateTest() {
        Mockito.when(balanceMapper.toEntity(balanceDto)).thenReturn(balance);
        balanceService.update(1L, balanceDto);
        Mockito.verify(balanceRepository).save(balance);
    }
}
