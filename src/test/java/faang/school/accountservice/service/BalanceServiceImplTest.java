package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private PaymentStatusChangePublisher paymentStatusChangePublisher;

    @Mock
    private ApplicationContext applicationContext;

    private BalanceDto balanceDto;
    private Balance balance;
    private final long accountId = 1L;
    private Balance toBalance;
    private Balance fromBalance;
    private long firstAccountId;
    private long secondAccountId;
    private PendingDto pendingDto;

    @BeforeEach
    public void init() {
        balance = new Balance();
        balance.setId(1L);

        balance.setVersion(1);

        balanceDto = new BalanceDto();
        balanceDto.setId(1L);
        balanceDto.setAccountId(1L);

        Mockito.lenient().when(mapper.toDto(balance))
                .thenReturn(balanceDto);
        Mockito.lenient().when(mapper.toEntity(balanceDto))
                .thenReturn(balance);
        Mockito.lenient().when(balanceJpaRepository.findById(accountId))
                .thenReturn(Optional.of(balance));
        Mockito.lenient().when(applicationContext.getBean(any(Class.class))).thenReturn(service);

        toBalance = new Balance();
        toBalance.setCurAuthBalance(BigDecimal.valueOf(30.0));

        fromBalance = new Balance();
        fromBalance.setCurAuthBalance(BigDecimal.valueOf(100.0));

        firstAccountId = 1L;
        secondAccountId = 2L;

        pendingDto = new PendingDto();
        pendingDto.setToAccountId(firstAccountId);
        pendingDto.setFromAccountId(secondAccountId);
    }

    @Test
    void create_whenOk() {
        service.create(balanceDto);

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
        Assertions.assertNotNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getCreatedAt());
    }

    @Test
    void getBalance_whenOk() {
        when(balanceJpaRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        service.getBalance(accountId);

        verify(mapper, times(1))
                .toDto(balance);
        Mockito.verify(balanceJpaRepository, Mockito.times(1))
                .findByAccountId(accountId);

    }

    @Test
    void getBalance_whenAccountNotExist() {
        Mockito.lenient().when(balanceJpaRepository.findById(accountId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.getBalance(accountId));

        Mockito.verify(mapper, Mockito.never())
                .toDto(balance);

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
        Assertions.assertThrows(DataValidationException.class, () -> service.update(balanceDto));

        verify(balanceJpaRepository, never()).save(any());
        verify(mapper, never()).toEntity(any());
    }

    @Test
    void testPaymentAuthorization_Success() {
        pendingDto.setAmount(BigDecimal.valueOf(20.0));
        when(balanceJpaRepository.findByAccountId(secondAccountId)).thenReturn(Optional.of(fromBalance));
        when(balanceJpaRepository.findByAccountId(firstAccountId)).thenReturn(Optional.of(toBalance));

        service.paymentAuthorization(pendingDto);

        assertEquals(BigDecimal.valueOf(80.0), fromBalance.getCurAuthBalance());
        assertEquals(BigDecimal.valueOf(50.0), toBalance.getCurAuthBalance());
        verify(balanceJpaRepository).save(fromBalance);
        verify(balanceJpaRepository).save(toBalance);
        verify(paymentStatusChangePublisher, never()).publish(pendingDto);
    }

    @Test
    void testPaymentAuthorization_Failure_InsufficientBalance() {
        pendingDto.setAmount(BigDecimal.valueOf(150.0));
        when(balanceJpaRepository.findByAccountId(firstAccountId)).thenReturn(Optional.of(fromBalance));
        when(balanceJpaRepository.findByAccountId(secondAccountId)).thenReturn(Optional.of(toBalance));

        service.paymentAuthorization(pendingDto);

        assertEquals(PendingStatus.CANCELED, pendingDto.getStatus());
        verify(paymentStatusChangePublisher, times(1)).publish(pendingDto);
        verify(balanceJpaRepository, never()).save(any());
    }

    @Test
    void testClearPayment() {
        PendingDto dto1 = new PendingDto();
        dto1.setFromAccountId(1L);
        dto1.setToAccountId(2L);
        dto1.setAmount(BigDecimal.valueOf(50.0));

        PendingDto dto2 = new PendingDto();
        dto2.setFromAccountId(3L);
        dto2.setToAccountId(4L);
        dto2.setAmount(BigDecimal.valueOf(30.0));

        Balance fromBalance1 = new Balance();
        fromBalance1.setCurFactBalance(BigDecimal.valueOf(200.0));
        Balance toBalance1 = new Balance();
        toBalance1.setCurFactBalance(BigDecimal.valueOf(100.0));

        Balance fromBalance2 = new Balance();
        fromBalance2.setCurFactBalance(BigDecimal.valueOf(120.0));
        Balance toBalance2 = new Balance();
        toBalance2.setCurFactBalance(BigDecimal.valueOf(80.0));

        when(balanceJpaRepository.findByAccountId(1L)).thenReturn(Optional.of(fromBalance1));
        when(balanceJpaRepository.findByAccountId(2L)).thenReturn(Optional.of(toBalance1));
        when(balanceJpaRepository.findByAccountId(3L)).thenReturn(Optional.of(fromBalance2));
        when(balanceJpaRepository.findByAccountId(4L)).thenReturn(Optional.of(toBalance2));

        List<PendingDto> pendingDtos = Arrays.asList(dto1, dto2);

        service.clearPayment(pendingDtos);

        assertEquals(BigDecimal.valueOf(150.0), fromBalance1.getCurFactBalance());
        assertEquals(BigDecimal.valueOf(150.0), toBalance1.getCurFactBalance());
        assertEquals(BigDecimal.valueOf(90.0), fromBalance2.getCurFactBalance());
        assertEquals(BigDecimal.valueOf(110.0), toBalance2.getCurFactBalance());

        verify(balanceJpaRepository, times(2)).save(fromBalance1);
        verify(balanceJpaRepository, times(2)).save(toBalance1);
        verify(balanceJpaRepository, times(1)).save(fromBalance2);
        verify(balanceJpaRepository, times(1)).save(toBalance2);
        verify(paymentStatusChangePublisher, times(2)).publish(any(PendingDto.class));
    }

    @Test
    void testGetBalanceDtoByAccountId() {
        when(balanceJpaRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        BalanceDto balanceDto = service.getBalanceByAccountId(accountId);

        assertEquals(this.balanceDto, balanceDto);
        verify(balanceJpaRepository).findByAccountId(accountId);
        verify(mapper).toDto(balance);
    }
}
