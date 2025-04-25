package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    private Long accountId = 1L;
    BalanceDto balanceDto;


    @BeforeEach
    void setUp() {
        balanceDto = BalanceDto.builder()
                .accountId(accountId)
                .actualBalance(new BigDecimal("100"))
                .authorizedBalance(new BigDecimal("50"))
                .build();
    }

    @Nested
    class Create {
        @Test
        void success() {
            Account account = new Account();
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(balanceMapper.toDto(any(Balance.class))).thenReturn(balanceDto);

            BalanceDto result = balanceService.create(accountId);

            ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);

            verify(balanceRepository).save(captor.capture());
            verify(balanceMapper).toDto(captor.capture());

            assertEquals(balanceDto, result);
            assertEquals(account, captor.getValue().getAccount());
        }
    }

    @Nested
    class GetByAccountId {
        @Test
        void success() {
            Balance balance = new Balance();
            when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

            balanceService.getByAccountId(accountId);

            verify(balanceRepository, times(1)).findByAccountId(accountId);
            verify(balanceMapper, times(1)).toDto(any(Balance.class));
        }

        @Test
        void entityNotFoundException() {
            when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                balanceService.getByAccountId(accountId);
            });

            verify(balanceRepository, times(1)).findByAccountId(accountId);
            verify(balanceMapper, never()).toDto(any(Balance.class));
        }
    }

}