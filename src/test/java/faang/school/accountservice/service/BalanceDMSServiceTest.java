package faang.school.accountservice.service;

import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.exception.EntityNotFound;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.BalanceDMS;
import faang.school.accountservice.repository.BalanceDMSRepository;
import faang.school.accountservice.service.balance.BalanceAuditService;
import faang.school.accountservice.service.balance.BalanceDMSService;
import faang.school.accountservice.validation.BalanceDMSValidator;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceDMSServiceTest {

    @Mock
    private BalanceDMSValidator balanceDMSValidator;

    @Mock
    private BalanceDMSRepository balanceDMSRepository;

    @Mock
    private BalanceAuditService balanceAuditService;

    @InjectMocks
    private BalanceDMSService balanceDMSService;

    private BalanceDMS senderBalanceDMS;
    private BalanceDMS recipientBalanceDMS;
    private AccountOperation operation;
    private UUID senderAccountId;
    private UUID recipientAccountId;
    private UUID operationId;
    private UUID paymentOperationId;

    @BeforeEach
    void setUp() {

        senderAccountId = UUID.randomUUID();
        recipientAccountId = UUID.randomUUID();
        operationId = UUID.randomUUID();
        paymentOperationId = UUID.randomUUID();

        senderBalanceDMS = new BalanceDMS();
        senderBalanceDMS.setAccountId(senderAccountId);
        senderBalanceDMS.setCurrency(Currency.USD);
        senderBalanceDMS.setClearBalance(new BigDecimal("1000"));
        senderBalanceDMS.setAuthBalance(new BigDecimal("500"));

        recipientBalanceDMS = new BalanceDMS();
        recipientBalanceDMS.setAccountId(recipientAccountId);
        recipientBalanceDMS.setCurrency(Currency.USD);
        recipientBalanceDMS.setClearBalance(new BigDecimal("2000"));
        recipientBalanceDMS.setAuthBalance(new BigDecimal("1000"));


        operation = new AccountOperation();
        operation.setId(operationId);
        operation.setPaymentOperationId(paymentOperationId);
        operation.setSenderAccountId(senderAccountId);
        operation.setRecipientAccountId(recipientAccountId);
        operation.setAmount(new BigDecimal("400"));
        operation.setCurrency(Currency.USD);
    }

    @Nested
    class ReserveFoundsTest {

        @Test
        void givenValidOperation_whenReserveFounds_thenBalanceUpdatedAndAudited() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            when(balanceDMSRepository.save(any(BalanceDMS.class))).thenReturn(senderBalanceDMS);

            balanceDMSService.reserveFounds(operation);

            verify(balanceDMSValidator, times(1)).authValidation(senderBalanceDMS, operation);
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, times(1)).save(senderBalanceDMS);
            verify(balanceAuditService, times(1)).setAudit(senderBalanceDMS, paymentOperationId);
            assertEquals(new BigDecimal("600"), senderBalanceDMS.getClearBalance());
            assertEquals(new BigDecimal("900"), senderBalanceDMS.getAuthBalance());
        }

        @Test
        void givenNonExistentSender_whenReserveFounds_thenThrowsEntityNotFound() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceDMSService.reserveFounds(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceDMSValidator, never()).authValidation(any(), any());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenReserveFounds_thenThrowsBalanceValidationException() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceDMSValidator)
                    .authValidation(senderBalanceDMS, operation);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSService.reserveFounds(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceDMSValidator, times(1)).authValidation(senderBalanceDMS, operation);
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }

    @Nested
    class ClearBalanceDMSTest {

        @Test
        void givenValidOperation_whenClearBalance_thenBalancesUpdatedAndAudited() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            when(balanceDMSRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.of(recipientBalanceDMS));
            when(balanceDMSRepository.save(any(BalanceDMS.class))).thenReturn(senderBalanceDMS).thenReturn(recipientBalanceDMS);

            balanceDMSService.clearBalance(operation);

            verify(balanceDMSValidator, times(1)).clearValidation(senderBalanceDMS, recipientBalanceDMS, operation.getAmount());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceDMSRepository, times(2)).save(any(BalanceDMS.class));
            verify(balanceAuditService, times(1)).setAudit(senderBalanceDMS, operationId);
            verify(balanceAuditService, times(1)).setAudit(recipientBalanceDMS, operationId);
            assertEquals(new BigDecimal("100"), senderBalanceDMS.getAuthBalance());
            assertEquals(new BigDecimal("2400"), recipientBalanceDMS.getClearBalance());
        }

        @Test
        void givenNonExistentSender_whenClearBalance_thenThrowsEntityNotFound() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceDMSService.clearBalance(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceDMSValidator, never()).clearValidation(any(), any(), any());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, never()).findByAccountId(recipientAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenNonExistentRecipient_whenClearBalance_thenThrowsEntityNotFound() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            when(balanceDMSRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceDMSService.clearBalance(operation));
            assertEquals("The recipient account does not exist.", exception.getMessage());
            verify(balanceDMSValidator, never()).clearValidation(any(), any(), any());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenClearBalance_thenThrowsBalanceValidationException() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            when(balanceDMSRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.of(recipientBalanceDMS));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceDMSValidator)
                    .clearValidation(senderBalanceDMS, recipientBalanceDMS, operation.getAmount());

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSService.clearBalance(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceDMSValidator, times(1)).clearValidation(senderBalanceDMS, recipientBalanceDMS, operation.getAmount());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }

    @Nested
    class CancelBalanceDMSTest {
        @Test
        void givenValidOperation_whenCancelBalance_thenBalanceUpdatedAndAudited() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            when(balanceDMSRepository.save(any(BalanceDMS.class))).thenReturn(senderBalanceDMS);

            balanceDMSService.cancelBalance(operation);

            verify(balanceDMSValidator, times(1)).cancelValidation(senderBalanceDMS, operation.getAmount());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, times(1)).save(senderBalanceDMS);
            verify(balanceAuditService, times(1)).setAudit(senderBalanceDMS, operationId);
            assertEquals(new BigDecimal("100"), senderBalanceDMS.getAuthBalance());
            assertEquals(new BigDecimal("1400"), senderBalanceDMS.getClearBalance());
        }

        @Test
        void givenNonExistentSender_whenCancelBalance_thenThrowsEntityNotFound() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceDMSService.cancelBalance(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceDMSValidator, never()).cancelValidation(any(), any());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenCancelBalance_thenThrowsBalanceValidationException() {
            when(balanceDMSRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalanceDMS));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceDMSValidator)
                    .cancelValidation(senderBalanceDMS, operation.getAmount());

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSService.cancelBalance(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceDMSValidator, times(1)).cancelValidation(senderBalanceDMS, operation.getAmount());
            verify(balanceDMSRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceDMSRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }
}