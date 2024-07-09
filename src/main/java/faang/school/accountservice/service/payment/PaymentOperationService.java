package faang.school.accountservice.service.payment;


import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.mapper.PaymentOperationMapper;
import faang.school.accountservice.repository.PaymentOperationRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.account.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOperationService {
    private final BalanceService balanceService;
    private final AccountService accountService;
    private final PaymentOperationRepository repository;
    private final PaymentOperationMapper mapper;


    @Transactional(readOnly = true)
    public boolean existsById(UUID paymentId) {
        return repository.existsById(paymentId);
    }

    @Transactional
    public void saveOperation(PaymentOperation pendingOperation) {
        repository.save(pendingOperation);
    }

    /**
     * Возвращает операцию оплаты по переданному номеру. Если в базе такой операции нет, выбрасывает EntityNotFoundException
     *
     * @param paymentId номер операции оплаты
     * @return сущность операции оплаты
     */
    @Transactional(readOnly = true)
    public PaymentOperation findById(UUID paymentId) {
        return repository.findById(paymentId);
    }

    public PaymentResponseDto authorizePayment(PaymentOperationDto paymentOperationDto) {
        PaymentResponseDto paymentResponseDto = mapper.toPaymentResponseDto(paymentOperationDto);

        verifyOperationExistence(paymentOperationDto, paymentResponseDto);
        createAndSaveOperation(paymentOperationDto, paymentResponseDto);

        return paymentResponseDto;
    }

    @Transactional
    public void createAndSaveOperation(PaymentOperationDto paymentOperationDto, PaymentResponseDto paymentResponseDto) {
        PaymentOperation paymentOperation;
        try {
            paymentOperation = createPaymentOperation(paymentOperationDto);
            reservePaymentAmount(paymentResponseDto, paymentOperation);
        } catch (NoSuchElementException e) {
            log.warn("Refusing the payment authorization, cause some of the accounts doesn't exist");
            refusePayment(paymentResponseDto, "Refused authorization cause of non-existence of some of accounts.");
        }
    }

    @Transactional(readOnly = true)
    public void verifyOperationExistence(PaymentOperationDto paymentOperationDto, PaymentResponseDto paymentResponseDto) {
        if (repository.existsAndIsntRefused(paymentOperationDto.getPaymentId())) {
            log.warn("Refusing the payment authorization, cause this payment already has been authorized");
            refusePayment(paymentResponseDto, "This payment already has been authorized");
        }
    }

    private void reservePaymentAmount(PaymentResponseDto paymentResponseDto, PaymentOperation paymentOperation) {
        if (!balanceService.reservePaymentAmount(paymentOperation.getSenderAccount(), paymentOperation.getAmount())) {
            log.warn("Refusing to authorize the payment because it was not possible to reserve money on the sender's balance.");
            refusePayment(paymentResponseDto, "Failed to reserve money on the sender's balance.");
            refusePayment(paymentOperation);
        } else {
            paymentOperation.setStatus(OperationStatus.AUTHORIZED);
            paymentResponseDto.setStatus(OperationStatus.AUTHORIZED);
        }

        repository.save(paymentOperation);
    }

    private void refusePayment(PaymentOperation paymentOperation) {
        paymentOperation.setStatus(OperationStatus.REFUSED);
    }

    private void refusePayment(PaymentResponseDto paymentResponse, String message) {
        paymentResponse.setStatus(OperationStatus.REFUSED);
        paymentResponse.setMessage(message);
    }

    private PaymentOperation createPaymentOperation(PaymentOperationDto paymentOperationDto) {
        Account debitAccount = accountService.getAccountModelByNumber(paymentOperationDto.getSenderAccountNumber());
        Account creditAccount = accountService.getAccountModelByNumber(paymentOperationDto.getReceiverAccountNumber());

        PaymentOperation pendingOperation = mapper.toModel(paymentOperationDto);
        pendingOperation.setSenderAccount(debitAccount);
        pendingOperation.setReceiverAccount(creditAccount);

        return pendingOperation;
    }
}
