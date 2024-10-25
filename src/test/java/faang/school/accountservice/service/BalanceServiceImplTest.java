package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.publisher.SubmitPaymentPublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @InjectMocks
    private BalanceServiceImpl service;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceJpaRepository balanceJpaRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceAuditMapper auditMapper;

    @Mock
    private BalanceMapper mapper;

    @Mock
    private SubmitPaymentPublisher submitPaymentPublisher;

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
        Mockito.lenient().when(balanceJpaRepository.findBalanceByAccount_Id(accountId))
                .thenReturn(balance);
    }

    @Test
    void create_whenOk() {
        service.create(balanceDto);
        balance.nextVersion();

        verify(balanceJpaRepository, times(1))
                .save(balance);
        verify(mapper, times(1))
                .toEntity(balanceDto);
        verify(balanceAuditRepository, times(1))
                .save(auditMapper.toEntity(balance));
    }

    @Test
    void update_whenOk() {
        final ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);

        service.update(balanceDto);

        verify(balanceJpaRepository, times(1))
                .save(captor.capture());
        verify(balanceAuditRepository, times(1))
                .save(auditMapper.toEntity(balance));

        Balance actual = captor.getValue();
        balanceDto.nextVersion();
        Assertions.assertNotNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getCreatedAt());
        assertEquals(balanceDto.getVersion(), actual.getVersion());
    }

    @Test
    void getBalance_whenOk() {
        when(accountRepository.existsById(accountId))
                .thenReturn(true);

        service.getBalance(accountId);

        verify(mapper, times(1))
                .toDto(balance);
        verify(balanceJpaRepository, times(1))
                .findBalanceByAccount_Id(accountId);

    }

    @Test
    void create_whenBadDto() {
        balanceDto.setId(-1);

        Assertions.assertThrows(DataValidationException.class, () -> service.create(balanceDto));

        balanceDto.setId(2);
        balanceDto.setAccountId(-2);
        Assertions.assertThrows(DataValidationException.class, () -> service.create(balanceDto));


        verify(balanceJpaRepository, never())
                .save(any());
        verify(mapper, never())
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

        verify(balanceJpaRepository, never()).save(any());
        verify(mapper, never()).toEntity(any());
    }

    @Test
    void testPaymentAuthorization_Success() {
        PendingDto pendingDto = new PendingDto();
        pendingDto.setFromAccountId(1L);
        pendingDto.setToAccountId(2L);
        pendingDto.setAmount(BigDecimal.valueOf(50.0));

        Balance fromBalance = new Balance();
        fromBalance.setCurAuthBalance(100.0);

        Balance toBalance = new Balance();
        toBalance.setCurAuthBalance(30.0);

        when(balanceJpaRepository.findBalanceByAccount_Id(1L)).thenReturn(fromBalance);
        when(balanceJpaRepository.findBalanceByAccount_Id(2L)).thenReturn(toBalance);

        service.paymentAuthorization(pendingDto);

        assertEquals(50.0, fromBalance.getCurAuthBalance());
        assertEquals(80.0, toBalance.getCurAuthBalance());
        verify(balanceJpaRepository).save(fromBalance);
        verify(balanceJpaRepository).save(toBalance);
        verify(submitPaymentPublisher, never()).publish(pendingDto);
    }

    @Test
    void testPaymentAuthorization_Failure_InsufficientBalance() {
        PendingDto pendingDto = new PendingDto();
        pendingDto.setFromAccountId(1L);
        pendingDto.setToAccountId(2L);
        pendingDto.setAmount(BigDecimal.valueOf(150.0));

        Balance fromBalance = new Balance();
        fromBalance.setCurAuthBalance(100.0);

        when(balanceJpaRepository.findBalanceByAccount_Id(1L)).thenReturn(fromBalance);

        service.paymentAuthorization(pendingDto);

        assertEquals(PendingStatus.CANCELED, pendingDto.getStatus());
        verify(submitPaymentPublisher, times(1)).publish(pendingDto);
        verify(balanceJpaRepository, never()).save(any());
    }

    @Test
    void testSubmitPayment() {
        PendingDto dto1 = new PendingDto();
        dto1.setFromAccountId(1L);
        dto1.setToAccountId(2L);
        dto1.setAmount(BigDecimal.valueOf(50.0));

        PendingDto dto2 = new PendingDto();
        dto2.setFromAccountId(3L);
        dto2.setToAccountId(4L);
        dto2.setAmount(BigDecimal.valueOf(30.0));

        Balance fromBalance1 = new Balance();
        fromBalance1.setCurFactBalance(200.0);
        Balance toBalance1 = new Balance();
        toBalance1.setCurFactBalance(100.0);

        Balance fromBalance2 = new Balance();
        fromBalance2.setCurFactBalance(120.0);
        Balance toBalance2 = new Balance();
        toBalance2.setCurFactBalance(80.0);

        when(balanceJpaRepository.findBalanceByAccount_Id(1L)).thenReturn(fromBalance1);
        when(balanceJpaRepository.findBalanceByAccount_Id(2L)).thenReturn(toBalance1);
        when(balanceJpaRepository.findBalanceByAccount_Id(3L)).thenReturn(fromBalance2);
        when(balanceJpaRepository.findBalanceByAccount_Id(4L)).thenReturn(toBalance2);

        List<PendingDto> pendingDtos = Arrays.asList(dto1, dto2);

        service.submitPayment(pendingDtos);

        assertEquals(150.0, fromBalance1.getCurFactBalance());
        assertEquals(150.0, toBalance1.getCurFactBalance());
        assertEquals(90.0, fromBalance2.getCurFactBalance());
        assertEquals(110.0, toBalance2.getCurFactBalance());

        verify(balanceJpaRepository, times(2)).save(fromBalance1);
        verify(balanceJpaRepository, times(2)).save(toBalance1);
        verify(balanceJpaRepository, times(1)).save(fromBalance2);
        verify(balanceJpaRepository, times(1)).save(toBalance2);
        verify(submitPaymentPublisher, times(2)).publish(any(PendingDto.class));
    }
}
