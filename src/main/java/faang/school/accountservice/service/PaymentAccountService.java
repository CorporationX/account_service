package faang.school.accountservice.service;

import faang.school.accountservice.dto.paymentAccount.CreatePaymentAccountDto;
import faang.school.accountservice.dto.paymentAccount.PaymentAccountDto;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.PaymentAccountMapper;
import faang.school.accountservice.model.PaymentAccount;
import faang.school.accountservice.repository.PaymentAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentAccountService {
    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentAccountMapper paymentAccountMapper;

    public PaymentAccountDto openPaymentAccount(CreatePaymentAccountDto dto) {
        log.info("OpenPaymentAccount: Request was received to open payment account {}", dto);
        PaymentAccount paymentAccount = paymentAccountMapper.createPaymentAccountDtoToPaymentAccount(dto);

        paymentAccount = paymentAccountRepository.save(paymentAccount);
        return paymentAccountMapper.toPaymentAccountDto(paymentAccount);
    }

    public PaymentAccountDto closePaymentAccount(String accountNumber) {
        log.info("ClosePaymentAccount: Attempting to close account with number {}", accountNumber);
        PaymentAccount paymentAccount = getByAccountNumber(accountNumber);
        validateThatAccountIsNotClosed(paymentAccount);

        paymentAccount.setPaymentAccountStatus(PaymentAccountStatus.CLOSED);

        paymentAccount = paymentAccountRepository.save(paymentAccount);
        return paymentAccountMapper.toPaymentAccountDto(paymentAccount);
    }

    public PaymentAccountDto blockPaymentAccount(String accountNumber) {
        log.info("BlockPaymentAccount: Attempting to block account with number {}", accountNumber);
        PaymentAccount paymentAccount = getByAccountNumber(accountNumber);
        validateThatAccountIsNotClosed(paymentAccount);

        paymentAccount.setPaymentAccountStatus(PaymentAccountStatus.BLOCKED);

        paymentAccount = paymentAccountRepository.save(paymentAccount);
        return paymentAccountMapper.toPaymentAccountDto(paymentAccount);
    }

    public PaymentAccountDto getPaymentAccountByNumber(String accountNumber) {
        log.info("GetPaymentAccount: Trying to get payment account {}", accountNumber);
        PaymentAccount paymentAccount = getByAccountNumber(accountNumber);

        return paymentAccountMapper.toPaymentAccountDto(paymentAccount);
    }

    public PaymentAccount getByAccountNumber(String accountNumber) {
        return paymentAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    private void validateThatAccountIsNotClosed(PaymentAccount paymentAccount) {
        if (paymentAccount.isClosed()) {
            throw new DataValidationException("Attempt to operate on a closed account");
        }
    }
}
