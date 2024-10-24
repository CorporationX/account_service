package faang.school.accountservice.service;

import faang.school.accountservice.dto.PaymentAccountDto;

public interface AccountService {
    PaymentAccountDto getPaymentAccount(Long id);
    PaymentAccountDto createPaymentAccount(PaymentAccountDto paymentAccountDto);
    PaymentAccountDto updatePaymentAccount(PaymentAccountDto paymentAccountDto);
    void deletePaymentAccount(Long id);
}
