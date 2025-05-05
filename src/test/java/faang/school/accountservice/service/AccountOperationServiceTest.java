package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountOperationResponse;
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
import static org.mockito.Mockito.doThrow;
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
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(operation);

            accountOperationService.processAuthorization(authorizationMessage);

            verify(accountOperationRepository, times(1)).existsByPaymentOperationId(paymentOperationId);
            verify(accountOperationMapper, times(1)).authMessageToAccountOperation(authorizationMessage);
            verify(balanceService, times(1)).reserveFounds(operation);
            verify(accountOperationRepository, times(1)).save(operation);
            assertEquals(OperationStatus.COMPLETED, operation.getOperationStatus());
        }

        @Test
        void givenExistingOperation_whenProcessAuthorization_thenOperationIgnored() {
            when(accountOperationRepository.existsByPaymentOperationId(paymentOperationId)).thenReturn(true);

            accountOperationService.processAuthorization(authorizationMessage);

            verify(accountOperationRepository, times(1)).existsByPaymentOperationId(paymentOperationId);
            verify(accountOperationMapper, never()).authMessageToAccountOperation(any());
            verify(balanceService, never()).reserveFounds(any());
            verify(accountOperationRepository, never()).save(any());
        }

        @Test
        void givenReserveFoundsFailure_whenProcessAuthorization_thenOperationSavedAsFailed() {
            AccountOperation operation = new AccountOperation();
            operation.setId(UUID.randomUUID());
            operation.setPaymentOperationId(paymentOperationId);

            when(accountOperationRepository.existsByPaymentOperationId(paymentOperationId)).thenReturn(false);
            when(accountOperationMapper.authMessageToAccountOperation(authorizationMessage)).thenReturn(operation);
            doThrow(new RuntimeException("Insufficient funds")).when(balanceService).reserveFounds(operation);
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(operation);

            accountOperationService.processAuthorization(authorizationMessage);

            verify(accountOperationRepository, times(1)).existsByPaymentOperationId(paymentOperationId);
            verify(accountOperationMapper, times(1)).authMessageToAccountOperation(authorizationMessage);
            verify(balanceService, times(1)).reserveFounds(operation);
            verify(accountOperationRepository, times(1)).save(operation);
            assertEquals(OperationStatus.FAILED, operation.getOperationStatus());
            assertEquals("Insufficient funds", operation.getErrorMessage());
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
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(clearingOperation);

            accountOperationService.processClearing(clearingMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(accountOperationRepository, times(1)).save(clearingOperation);
            verify(balanceService, times(1)).clearBalance(clearingOperation);
            assertEquals(OperationType.CLEARING, clearingOperation.getOperationType());
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
            verify(balanceService, never()).clearBalance(any());
            verify(accountOperationRepository, never()).save(any());
        }

        @Test
        void givenClearBalanceFailure_whenProcessClearing_thenOperationSavedAsFailed() {
            AccountOperation clearingOperation = new AccountOperation();
            clearingOperation.setId(operationId);
            clearingOperation.setOperationType(OperationType.CLEARING);

            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.of(authOperation));
            when(accountOperationRepository.existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING))
                    .thenReturn(false);
            when(accountOperationMapper.cloneOperation(authOperation, operationId)).thenReturn(clearingOperation);
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(clearingOperation);
            doThrow(new RuntimeException("Insufficient funds")).when(balanceService).clearBalance(clearingOperation);

            accountOperationService.processClearing(clearingMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(accountOperationRepository, times(2)).save(clearingOperation);
            verify(balanceService, times(1)).clearBalance(clearingOperation);
            assertEquals(OperationStatus.FAILED, clearingOperation.getOperationStatus());
            assertEquals("Insufficient funds", clearingOperation.getErrorMessage());
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
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(cancellationOperation);

            accountOperationService.processCancellation(cancellationMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(accountOperationRepository, times(1)).save(cancellationOperation);
            verify(balanceService, times(1)).cancelBalance(cancellationOperation);
            assertEquals(OperationType.CANCELLATION, cancellationOperation.getOperationType());
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
            verify(balanceService, never()).cancelBalance(any());
            verify(accountOperationRepository, never()).save(any());
        }

        @Test
        void givenCancelBalanceFailure_whenProcessCancellation_thenOperationSavedAsFailed() {
            AccountOperation cancellationOperation = new AccountOperation();
            cancellationOperation.setId(operationId);
            cancellationOperation.setOperationType(OperationType.CANCELLATION);

            when(accountOperationRepository.findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED))
                    .thenReturn(Optional.of(authOperation));
            when(accountOperationRepository.existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION))
                    .thenReturn(false);
            when(accountOperationMapper.cloneOperation(authOperation, operationId)).thenReturn(cancellationOperation);
            when(accountOperationRepository.save(any(AccountOperation.class))).thenReturn(cancellationOperation);
            doThrow(new RuntimeException("Insufficient funds")).when(balanceService).cancelBalance(cancellationOperation);

            accountOperationService.processCancellation(cancellationMessage);

            verify(accountOperationRepository, times(1))
                    .findAuthOperation(authorizationId, OperationType.AUTHORIZATION, OperationStatus.COMPLETED);
            verify(accountOperationRepository, times(1))
                    .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION);
            verify(accountOperationMapper, times(1)).cloneOperation(authOperation, operationId);
            verify(accountOperationRepository, times(2)).save(cancellationOperation);
            verify(balanceService, times(1)).cancelBalance(cancellationOperation);
            assertEquals(OperationStatus.FAILED, cancellationOperation.getOperationStatus());
            assertEquals("Insufficient funds", cancellationOperation.getErrorMessage());
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

            AccountOperationResponse response = accountOperationService.getOperation(operationId);

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