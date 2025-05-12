package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountOperationViewDto;
import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.dto.OperationStatus;
import faang.school.accountservice.dto.OperationType;
import faang.school.accountservice.dto.message.AuthorizationMessage;
import faang.school.accountservice.dto.message.CancellationMessage;
import faang.school.accountservice.dto.message.ClearingMessage;
import faang.school.accountservice.exception.OperationNotFound;
import faang.school.accountservice.mapper.AccountOperationMapper;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.repository.AccountOperationRepository;
import faang.school.accountservice.service.account.AccountOperationService;
import faang.school.accountservice.service.account.OperationProcessor;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOperationServiceTest {

    @Mock
    private AccountOperationRepository accountOperationRepository;

    @Mock
    private AccountOperationMapper accountOperationMapper;

    @Mock
    private BalanceService balanceService;

    @Mock
    private OperationProcessor operationProcessor;

    @InjectMocks
    private AccountOperationService accountOperationService;

    private UUID operationId;
    private UUID paymentOperationId;
    private UUID authorizationId;
    private UUID senderAccountId;
    private UUID recipientAccountId;
    private AccountOperation authOperation;
    private AuthorizationMessage authorizationMessage;
    private ClearingMessage clearingMessage;
    private CancellationMessage cancellationMessage;

    @BeforeEach
    void setUp() {
        operationId = UUID.randomUUID();
        paymentOperationId = UUID.randomUUID();
        authorizationId = UUID.randomUUID();
        senderAccountId = UUID.randomUUID();
        recipientAccountId = UUID.randomUUID();

        authOperation = new AccountOperation();
        authOperation.setId(authorizationId);
        authOperation.setPaymentOperationId(paymentOperationId);
        authOperation.setSenderAccountId(senderAccountId);
        authOperation.setRecipientAccountId(recipientAccountId);
        authOperation.setAmount(new BigDecimal("100"));
        authOperation.setCurrency(Currency.USD);
        authOperation.setOperationType(OperationType.AUTHORIZATION);
        authOperation.setOperationStatus(OperationStatus.COMPLETED);

        authorizationMessage = new AuthorizationMessage();
        authorizationMessage.setOperationId(paymentOperationId);
        authorizationMessage.setSenderAccountId(senderAccountId);
        authorizationMessage.setRecipientAccountId(recipientAccountId);
        authorizationMessage.setAmount(new BigDecimal("100"));
        authorizationMessage.setCurrency("USD");

        clearingMessage = new ClearingMessage();
        clearingMessage.setOperationId(operationId);
        clearingMessage.setAuthorizationId(authorizationId);

        cancellationMessage = new CancellationMessage();
        cancellationMessage.setOperationId(operationId);
        cancellationMessage.setAuthorizationId(authorizationId);
    }

    @Nested
    class ProcessAuthorizationTests {

        @Test
        void givenValidMessage_whenProcessAuthorization_thenOperationSavedAsCompleted() {
            AccountOperation operation = new AccountOperation();
            operation.setId(UUID.randomUUID());
            operation.setPaymentOperationId(paymentOperationId);
            operation.setOperationStatus(OperationStatus.COMPLETED);

            when(accountOperationRepository.existsByPaymentOperationId(paymentOperationId)).thenReturn(false);
            when(accountOperationMapper.authMessageToAccountOperation(authorizationMessage)).thenReturn(operation);

            accountOperationService.processAuthorization(authorizationMessage);

            verify(accountOperationRepository, times(1)).existsByPaymentOperationId(paymentOperationId);
            verify(accountOperationMapper, times(1)).authMessageToAccountOperation(authorizationMessage);
            verify(operationProcessor, times(1)).processOperation(
                    eq(operation),
                    eq(paymentOperationId),
                    eq(OperationType.AUTHORIZATION),
                    any(Runnable.class)
            );
        }

        @Test
        void givenExistingOperation_whenProcessAuthorization_thenOperationIgnored() {
            when(accountOperationRepository.existsByPaymentOperationId(paymentOperationId)).thenReturn(true);

            accountOperationService.processAuthorization(authorizationMessage);

            verify(accountOperationRepository, times(1)).existsByPaymentOperationId(paymentOperationId);
            verify(accountOperationMapper, never()).authMessageToAccountOperation(any());
            verify(operationProcessor, never()).processOperation(any(), any(), any(), any());
        }
    }

    @Nested
    class ProcessClearingTests {

        @Test
        void givenValidMessage_whenProcessClearing_thenOperationSavedAsCompleted() {
            AccountOperation clearingOperation = new AccountOperation();
            clearingOperation.setId(operationId);
            clearingOperation.setOperationType(OperationType.CLEARING);
            clearingOperation.setOperationStatus(OperationStatus.COMPLETED);

            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.of(authOperation));
            when(accountOperationRepository.existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING))
                    .thenReturn(false);
            when(accountOperationMapper.cloneOperation(authOperation, operationId)).thenReturn(clearingOperation);

            accountOperationService.processClearing(clearingMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(operationProcessor, times(1)).processOperation(
                    eq(clearingOperation),
                    eq(operationId),
                    eq(OperationType.CLEARING),
                    any(Runnable.class)
            );
        }

        @Test
        void givenNonExistentAuthOperation_whenProcessClearing_thenThrowsOperationNotFound() {
            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.empty());

            OperationNotFound exception = assertThrows(OperationNotFound.class,
                    () -> accountOperationService.processClearing(clearingMessage));
            assertEquals("The operation has not found.", exception.getMessage());
            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, never()).existsByPaymentOperationIdAndOperationType(any(), any());
            verify(accountOperationMapper, never()).cloneOperation(any(), any());
            verify(operationProcessor, never()).processOperation(any(), any(), any(), any());
        }
    }

    @Nested
    class ProcessCancellationTests {

        @Test
        void givenValidMessage_whenProcessCancellation_thenOperationSavedAsCompleted() {
            AccountOperation cancellationOperation = new AccountOperation();
            cancellationOperation.setId(operationId);
            cancellationOperation.setOperationType(OperationType.CANCELLATION);
            cancellationOperation.setOperationStatus(OperationStatus.COMPLETED);

            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.of(authOperation));
            when(accountOperationRepository.existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION))
                    .thenReturn(false);
            when(accountOperationMapper.cloneOperation(authOperation, operationId)).thenReturn(cancellationOperation);

            accountOperationService.processCancellation(cancellationMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(operationProcessor, times(1)).processOperation(
                    eq(cancellationOperation),
                    eq(operationId),
                    eq(OperationType.CANCELLATION),
                    any(Runnable.class)
            );
        }

        @Test
        void givenNonExistentAuthOperation_whenProcessCancellation_thenThrowsOperationNotFound() {
            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.empty());

            OperationNotFound exception = assertThrows(OperationNotFound.class,
                    () -> accountOperationService.processCancellation(cancellationMessage));
            assertEquals("The operation has not found.", exception.getMessage());
            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, never()).existsByPaymentOperationIdAndOperationType(any(), any());
            verify(accountOperationMapper, never()).cloneOperation(any(), any());
            verify(operationProcessor, never()).processOperation(any(), any(), any(), any());
        }
    }

    @Nested
    class GetOperationTests {

        @Test
        void givenExistingOperation_whenGetOperation_thenReturnsResponse() {
            AccountOperation operation = new AccountOperation();
            operation.setId(operationId);
            operation.setAuthorizationId(authorizationId);
            operation.setOperationType(OperationType.AUTHORIZATION);
            operation.setOperationStatus(OperationStatus.COMPLETED);
            operation.setErrorMessage(null);

            when(accountOperationRepository.findById(operationId)).thenReturn(Optional.of(operation));

            AccountOperationViewDto response = accountOperationService.getOperation(operationId);

            verify(accountOperationRepository, times(1)).findById(operationId);
            assertEquals(authorizationId, response.id());
            assertEquals(OperationStatus.COMPLETED, response.status());
            assertEquals(OperationType.AUTHORIZATION, response.operationType());
            assertNull(response.message());
        }

        @Test
        void givenNonExistentOperation_whenGetOperation_thenThrowsOperationNotFound() {
            when(accountOperationRepository.findById(operationId)).thenReturn(Optional.empty());

            OperationNotFound exception = assertThrows(OperationNotFound.class,
                    () -> accountOperationService.getOperation(operationId));
            assertEquals("The operation has not found.", exception.getMessage());
            verify(accountOperationRepository, times(1)).findById(operationId);
        }
    }
}