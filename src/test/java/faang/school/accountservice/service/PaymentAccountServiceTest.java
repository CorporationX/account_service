package faang.school.accountservice.service;

import faang.school.accountservice.dto.paymentAccount.CreatePaymentAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.PaymentAccountType;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.PaymentAccountMapper;
import faang.school.accountservice.model.PaymentAccount;
import faang.school.accountservice.repository.PaymentAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentAccountServiceTest {

    @InjectMocks
    private PaymentAccountService paymentAccountService;

    @Mock
    private PaymentAccountRepository paymentAccountRepository;
    @Spy
    private PaymentAccountMapper mapper = Mappers.getMapper(PaymentAccountMapper.class);

    private PaymentAccount paymentAccount;

    @BeforeEach
    public void setUp() {
        paymentAccount = PaymentAccount.builder()
                .id(1L)
                .accountNumber("4444555566667777")
                .paymentAccountStatus(PaymentAccountStatus.ACTIVE)
                .accountType(PaymentAccountType.ACCUMULATIVE_ACCOUNT)
                .ownerId(1L)
                .currency(Currency.RUB)
                .build();
    }

    @Test
    public void testOpenPaymentAccount() {
        CreatePaymentAccountDto createDto = CreatePaymentAccountDto.builder()
                .accountType(PaymentAccountType.ACCUMULATIVE_ACCOUNT)
                .currency(Currency.RUB)
                .ownerId(1L)
                .build();
        when(paymentAccountRepository.save(any())).thenReturn(paymentAccount);

        paymentAccountService.openPaymentAccount(createDto);

        verify(paymentAccountRepository).save(any());
    }

    @Test
    public void testClosePaymentAccount() {
        when(paymentAccountRepository.findByAccountNumber(any())).thenReturn(Optional.ofNullable(paymentAccount));
        when(paymentAccountRepository.save(any())).thenReturn(paymentAccount);

        paymentAccountService.closePaymentAccount(paymentAccount.getAccountNumber());

        verify(paymentAccountRepository).findByAccountNumber(any());
        verify(paymentAccountRepository).save(any());
    }

    @Test
    public void testCloseAlreadyClosedPaymentAccount() {
        paymentAccount.setPaymentAccountStatus(PaymentAccountStatus.CLOSED);
        when(paymentAccountRepository.findByAccountNumber(any())).thenReturn(Optional.ofNullable(paymentAccount));

        Assertions.assertThrows(DataValidationException.class,
                () -> paymentAccountService.closePaymentAccount(paymentAccount.getAccountNumber()));
    }

    @Test
    public void testBlockPaymentAccount() {
        when(paymentAccountRepository.findByAccountNumber(any())).thenReturn(Optional.ofNullable(paymentAccount));
        when(paymentAccountRepository.save(any())).thenReturn(paymentAccount);

        paymentAccountService.blockPaymentAccount(paymentAccount.getAccountNumber());

        verify(paymentAccountRepository).findByAccountNumber(any());
        verify(paymentAccountRepository).save(any());
    }

    @Test
    public void testBlockAlreadyClosedPaymentAccount() {
        paymentAccount.setPaymentAccountStatus(PaymentAccountStatus.CLOSED);
        when(paymentAccountRepository.findByAccountNumber(any())).thenReturn(Optional.ofNullable(paymentAccount));

        Assertions.assertThrows(DataValidationException.class,
                () -> paymentAccountService.blockPaymentAccount(paymentAccount.getAccountNumber()));
    }

    @Test
    public void testGetPaymentAccount() {
        String accountNumber = paymentAccount.getAccountNumber();
        when(paymentAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.ofNullable(paymentAccount));

        paymentAccountService.getByAccountNumber(accountNumber);

        verify(paymentAccountRepository).findByAccountNumber(accountNumber);
    }

}
