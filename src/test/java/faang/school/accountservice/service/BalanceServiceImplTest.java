package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {
    @InjectMocks
    private BalanceServiceImpl service;

    @Mock
    private BalanceJpaRepository balanceJpaRepository;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper mapper;

    private BalanceDto balanceDto;
    private Balance balance;
    private final long accountId = 1L;

    @BeforeEach
    public void init() {
        balance = new Balance();
        balance.setId(1L);

        balance.setVersion(1);

        balanceDto = new BalanceDto();
        balanceDto.setId(1L);
        balanceDto.setVersion(1);
        balanceDto.setAccountId(1L);

        Mockito.lenient().when(mapper.toDto(balance))
                .thenReturn(balanceDto);
        Mockito.lenient().when(mapper.toEntity(balanceDto))
                .thenReturn(balance);
        Mockito.lenient().when(balanceJpaRepository.findBalanceByAccountId(accountId))
                .thenReturn(balance);
    }

    @Test
    void create_whenOk() {
        service.create(balanceDto);

        Mockito.verify(balanceJpaRepository, Mockito.times(1))
                .save(balance);
        Mockito.verify(mapper, Mockito.times(1))
                .toEntity(balanceDto);
    }

    @Test
    void update_whenOk() {
        final ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);

        service.update(balanceDto);

        Mockito.verify(balanceJpaRepository, Mockito.times(1))
                .save(captor.capture());

        Balance actual = captor.getValue();
        Assertions.assertNotNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getCreatedAt());
        Assertions.assertEquals(balanceDto.getVersion(), actual.getVersion());
    }

    @Test
    void getBalance_whenOk() {
        Mockito.when(accountRepository.existsById(accountId))
                .thenReturn(true);

        service.getBalance(accountId);

        Mockito.verify(mapper, Mockito.times(1))
                .toDto(balance);
        Mockito.verify(balanceJpaRepository, Mockito.times(1))
                .findBalanceByAccountId(accountId);

    }

    @Test
    void create_whenBadDto() {
        balanceDto.setId(-1);

        Assertions.assertThrows(DataValidationException.class, () -> service.create(balanceDto));

        balanceDto.setId(2);
        balanceDto.setAccountId(-2);
        Assertions.assertThrows(DataValidationException.class, () -> service.create(balanceDto));


        Mockito.verify(balanceJpaRepository, Mockito.never())
                .save(any());
        Mockito.verify(mapper, Mockito.never())
                .toEntity(any());
    }

    @Test
    void update_whenBadDto() {
        balanceDto.setId(-1);

        Assertions.assertThrows(DataValidationException.class, () -> service.update(balanceDto));

        balanceDto.setId(2);
        balanceDto.setAccountId(-2);
        Assertions.assertThrows(DataValidationException.class, () -> service.update(balanceDto));

        balanceDto.setAccountId(2);
        balanceDto.setVersion(-1);
        Assertions.assertThrows(DataValidationException.class, () -> service.update(balanceDto));


        Mockito.verify(balanceJpaRepository, Mockito.never())
                .save(any());
        Mockito.verify(mapper, Mockito.never())
                .toEntity(any());
    }
}
