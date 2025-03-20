package faang.school.accountservice.service.transfer;

import faang.school.accountservice.dto.transfer_request.TransferRequestDto;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
import faang.school.accountservice.exception.NonRetryableException;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.exception.non_retryable.EqualAccountException;
import faang.school.accountservice.exception.non_retryable.NotActiveAccountException;
import faang.school.accountservice.exception.non_retryable.NotEnoughFundsException;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferValidatorTest {

    @Mock
    private TransferService transferService;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private TransferValidator transferValidator;

    private static final UUID TRANSFER_ID = UUID.randomUUID();
    private static final String SENDER_ACCOUNT = "123456789123";
    private static final String RECEIVER_ACCOUNT = "987654321987";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);

    @Test
    void validateAccounts_success() {
        TransferRequestDto request = createRequest(SENDER_ACCOUNT, RECEIVER_ACCOUNT);
        assertDoesNotThrow(() -> transferValidator.validateAccounts(request));

        verify(accountService, times(1)).checkAccountIsActive(SENDER_ACCOUNT);
        verify(accountService, times(1)).checkAccountIsActive(RECEIVER_ACCOUNT);
        verify(accountService, times(1)).checkCurrencyMismatch(SENDER_ACCOUNT, request.currency());
        verify(accountService, times(1)).checkCurrencyMismatch(RECEIVER_ACCOUNT, request.currency());
    }

    @Test
    void validateAccounts_throwsEqualAccountException() {
        TransferRequestDto request = createRequest(SENDER_ACCOUNT, SENDER_ACCOUNT);

        EqualAccountException exception = assertThrows(EqualAccountException.class, () -> {
            transferValidator.validateAccounts(request);
        });

        assertEquals("Equal accounts. Process failed", exception.getMessage());
    }

    @Test
    void validateAccounts_throwsNotActiveAccountException() {
        doThrow(new NotActiveAccountException("Account is not active"))
                .when(accountService).checkAccountIsActive(SENDER_ACCOUNT);

        TransferRequestDto request = createRequest(SENDER_ACCOUNT, RECEIVER_ACCOUNT);

        NotActiveAccountException exception = assertThrows(NotActiveAccountException.class, () -> {
            transferValidator.validateAccounts(request);
        });

        assertEquals("Account is not active", exception.getMessage());
    }

    @Test
    void getTransferOrThrow_returnsTransferRequest() {
        TransferRequest transferRequest = new TransferRequest();
        when(transferService.getById(TRANSFER_ID)).thenReturn(transferRequest);

        Consumer<TransferStatus> responseSender = mock(Consumer.class);
        TransferRequest result = transferValidator.getTransferOrThrow(TRANSFER_ID, responseSender);

        assertNotNull(result);
        assertEquals(transferRequest, result);
        verify(responseSender, never()).accept(any());
    }

    @Test
    void getTransferOrThrow_handlesNotFound() {
        when(transferService.getById(TRANSFER_ID)).thenThrow(new EntityNotFoundException("Not found"));

        Consumer<TransferStatus> responseSender = mock(Consumer.class);
        TransferRequest result = transferValidator.getTransferOrThrow(TRANSFER_ID, responseSender);

        assertNull(result);
        verify(responseSender, times(1)).accept(TransferStatus.PAYMENT_NOT_FOUND);
    }

    @Test
    void handleValidationException_mapsExceptionToStatus() {
        Consumer<TransferStatus> responseSender = mock(Consumer.class);

        transferValidator.handleValidationException(new NotEnoughFundsException("Not enough funds"), TRANSFER_ID, responseSender);
        verify(responseSender, times(1)).accept(TransferStatus.NOT_ENOUGH_FUNDS);

        transferValidator.handleValidationException(new EntityNotFoundException("Entity not found"), TRANSFER_ID, responseSender);
        verify(responseSender, times(1)).accept(TransferStatus.ERROR);

        transferValidator.handleValidationException(new EqualAccountException("Equal accounts"), TRANSFER_ID, responseSender);
        verify(responseSender, times(2)).accept(TransferStatus.ERROR);
    }

    @Test
    void handleValidationException_throwsNonRetryableExceptionForUnknownErrors() {
        Consumer<TransferStatus> responseSender = mock(Consumer.class);

        NonRetryableException exception = assertThrows(NonRetryableException.class, () -> {
            transferValidator.handleValidationException(new RuntimeException("Unexpected error"), TRANSFER_ID, responseSender);
        });

        assertEquals("Unexpected error", exception.getMessage());
        verify(responseSender, never()).accept(any());
    }

    private TransferRequestDto createRequest(String sender, String receiver) {
        return TransferRequestDto.builder()
                .id(TRANSFER_ID)
                .senderAccountNumber(sender)
                .receiverAccountNumber(receiver)
                .amount(AMOUNT)
                .currency(Currency.USD)
                .build();
    }
}
