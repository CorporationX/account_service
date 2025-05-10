package faang.school.accountservice.service;

import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.exception.EntityNotFound;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.balance.BalanceAuditService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.validation.BalanceValidator;
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
class BalanceServiceTest {

    @Mock
    private BalanceValidator balanceValidator;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceAuditService balanceAuditService;

    @InjectMocks
    private BalanceService balanceService;

    private Balance senderBalance;
    private Balance recipientBalance;
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

        senderBalance = new Balance();
        senderBalance.setAccountId(senderAccountId);
        senderBalance.setCurrency(Currency.USD);
        senderBalance.setClearBalance(new BigDecimal("1000"));
        senderBalance.setAuthBalance(new BigDecimal("500"));

        recipientBalance = new Balance();
        recipientBalance.setAccountId(recipientAccountId);
        recipientBalance.setCurrency(Currency.USD);
        recipientBalance.setClearBalance(new BigDecimal("2000"));
        recipientBalance.setAuthBalance(new BigDecimal("1000"));


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
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            when(balanceRepository.save(any(Balance.class))).thenReturn(senderBalance);

            balanceService.reserveFounds(operation);

            verify(balanceValidator, times(1)).authValidation(senderBalance, operation);
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, times(1)).save(senderBalance);
            verify(balanceAuditService, times(1)).setAudit(senderBalance, paymentOperationId);
            assertEquals(new BigDecimal("600"), senderBalance.getClearBalance());
            assertEquals(new BigDecimal("900"), senderBalance.getAuthBalance());
        }

        @Test
        void givenNonExistentSender_whenReserveFounds_thenThrowsEntityNotFound() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceService.reserveFounds(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceValidator, never()).authValidation(any(), any());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenReserveFounds_thenThrowsBalanceValidationException() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceValidator)
                    .authValidation(senderBalance, operation);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceService.reserveFounds(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceValidator, times(1)).authValidation(senderBalance, operation);
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }

    @Nested
    class ClearBalanceTest {

        @Test
        void givenValidOperation_whenClearBalance_thenBalancesUpdatedAndAudited() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            when(balanceRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.of(recipientBalance));
            when(balanceRepository.save(any(Balance.class))).thenReturn(senderBalance).thenReturn(recipientBalance);

            balanceService.clearBalance(operation);

            verify(balanceValidator, times(1)).clearValidation(senderBalance, recipientBalance, operation.getAmount());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceRepository, times(2)).save(any(Balance.class));
            verify(balanceAuditService, times(1)).setAudit(senderBalance, operationId);
            verify(balanceAuditService, times(1)).setAudit(recipientBalance, operationId);
            assertEquals(new BigDecimal("100"), senderBalance.getAuthBalance());
            assertEquals(new BigDecimal("2400"), recipientBalance.getClearBalance());
        }

        @Test
        void givenNonExistentSender_whenClearBalance_thenThrowsEntityNotFound() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceService.clearBalance(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceValidator, never()).clearValidation(any(), any(), any());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, never()).findByAccountId(recipientAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenNonExistentRecipient_whenClearBalance_thenThrowsEntityNotFound() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            when(balanceRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceService.clearBalance(operation));
            assertEquals("The recipient account does not exist.", exception.getMessage());
            verify(balanceValidator, never()).clearValidation(any(), any(), any());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenClearBalance_thenThrowsBalanceValidationException() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            when(balanceRepository.findByAccountId(recipientAccountId)).thenReturn(Optional.of(recipientBalance));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceValidator)
                    .clearValidation(senderBalance, recipientBalance, operation.getAmount());

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceService.clearBalance(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceValidator, times(1)).clearValidation(senderBalance, recipientBalance, operation.getAmount());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, times(1)).findByAccountId(recipientAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }

    @Nested
    class CancelBalanceTest {
        @Test
        void givenValidOperation_whenCancelBalance_thenBalanceUpdatedAndAudited() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            when(balanceRepository.save(any(Balance.class))).thenReturn(senderBalance);

            balanceService.cancelBalance(operation);

            verify(balanceValidator, times(1)).cancelValidation(senderBalance, operation.getAmount());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, times(1)).save(senderBalance);
            verify(balanceAuditService, times(1)).setAudit(senderBalance, operationId);
            assertEquals(new BigDecimal("100"), senderBalance.getAuthBalance());
            assertEquals(new BigDecimal("1400"), senderBalance.getClearBalance());
        }

        @Test
        void givenNonExistentSender_whenCancelBalance_thenThrowsEntityNotFound() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.empty());

            EntityNotFound exception = assertThrows(EntityNotFound.class,
                    () -> balanceService.cancelBalance(operation));
            assertEquals("The sender account does not exist.", exception.getMessage());
            verify(balanceValidator, never()).cancelValidation(any(), any());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }

        @Test
        void givenInvalidOperation_whenCancelBalance_thenThrowsBalanceValidationException() {
            when(balanceRepository.findByAccountId(senderAccountId)).thenReturn(Optional.of(senderBalance));
            doThrow(new BalanceValidationException("Invalid operation")).when(balanceValidator)
                    .cancelValidation(senderBalance, operation.getAmount());

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceService.cancelBalance(operation));
            assertEquals("Invalid operation", exception.getMessage());
            verify(balanceValidator, times(1)).cancelValidation(senderBalance, operation.getAmount());
            verify(balanceRepository, times(1)).findByAccountId(senderAccountId);
            verify(balanceRepository, never()).save(any());
            verify(balanceAuditService, never()).setAudit(any(), any());
        }
    }
}