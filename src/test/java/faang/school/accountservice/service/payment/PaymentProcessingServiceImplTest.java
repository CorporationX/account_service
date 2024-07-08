package faang.school.accountservice.service.payment;

import faang.school.accountservice.client.PaymentServiceClient;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.mapper.PaymentKeyGenerator;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.account.AccountService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceImplTest {
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @Mock
    private AccountService accountService;
    @Mock
    private BalanceService balanceService;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PaymentHistoryService paymentHistoryService;
    @Mock
    private PaymentKeyGenerator paymentKeyGenerator;
    @InjectMocks
    private PaymentProcessingServiceImpl paymentProcessingService;

    private PaymentEvent paymentEvent;
    private AccountDto accountDto;
    private BalanceDto balanceDto;

    @BeforeEach
    void setUp() {
        paymentEvent = PaymentEvent.builder()
                .paymentId(1L)
                .requesterNumber(new BigInteger("88005553535"))
                .receiverNumber(new BigInteger("84953630000"))
                .amount(new BigDecimal(30))
                .build();
        accountDto = AccountDto.builder()
                .id(1L)
                .balance(new BigDecimal(200))
                .build();
        balanceDto = BalanceDto.builder()
                .id(1L)
                .currentBalance(new BigDecimal(100))
                .authorizedBalance(new BigDecimal(200))
                .build();
    }

    @Test
    public void whenReserveMoneyAndPaymentCanceled() {
        when(accountService.existsByNumber(any())).thenReturn(false);
        when(paymentKeyGenerator.generateKey(any())).thenReturn(IDEMPOTENCY_KEY);
        paymentProcessingService.reserveMoney(paymentEvent);
        verify(paymentServiceClient).cancelPayment(anyLong());
    }

    @Test
    public void whenReserveMoneySuccessfully() {
        when(accountService.existsByNumber(any())).thenReturn(true);
        when(accountService.getByNumber(any())).thenReturn(accountDto);
        when(balanceService.getBalance(any())).thenReturn(balanceDto);
        when(paymentKeyGenerator.generateKey(any())).thenReturn(IDEMPOTENCY_KEY);
        paymentProcessingService.reserveMoney(paymentEvent);
        verify(balanceService).updateBalance(any(), any(), any());
        verify(paymentHistoryService).save(paymentEvent);
    }

    @Test
    public void whenCancelPaymentSuccessfully() {
        when(accountService.getByNumber(any())).thenReturn(accountDto);
        when(balanceService.getBalance(any())).thenReturn(balanceDto);
        when(paymentKeyGenerator.generateKey(any())).thenReturn(IDEMPOTENCY_KEY);
        paymentProcessingService.cancelPayment(paymentEvent);
        verify(balanceService).updateBalance(any(), any(), any());
        verify(paymentHistoryService).save(paymentEvent);
    }

    @Test
    public void whenClearingSuccessfully() {
        when(accountService.getByNumber(any())).thenReturn(accountDto);
        when(balanceService.getBalance(any())).thenReturn(balanceDto);
        when(paymentKeyGenerator.generateKey(any())).thenReturn(IDEMPOTENCY_KEY);
        paymentProcessingService.clearing(paymentEvent);
        verify(balanceService, times(2)).updateBalance(any(), any(), any());
        verify(paymentHistoryService).save(paymentEvent);
    }

    @Test
    public void whenPassPaymentSuccessfully() {
        paymentProcessingService.passPayment(paymentEvent);
        verify(paymentServiceClient).passPayment(anyLong());
    }
}