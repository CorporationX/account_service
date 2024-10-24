package faang.school.accountservice.service;

import faang.school.accountservice.dto.PaymentAccountDto;
import faang.school.accountservice.entity.PaymentAccount;
import faang.school.accountservice.mapper.PaymentAccountMapper;
import faang.school.accountservice.repository.PaymentAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentAccountMapper paymentAccountMapper;

    @Transactional(readOnly = true)
    @Override
    public PaymentAccountDto getPaymentAccount(Long id) {
        PaymentAccount paymentAccount = paymentAccountRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("no payment account with id " + id));
        return paymentAccountMapper.toDto(paymentAccount);
    }

    @Transactional
    @Override
    public PaymentAccountDto createPaymentAccount(PaymentAccountDto paymentAccountDto) {
        PaymentAccount paymentAccount = paymentAccountMapper.toEntity(paymentAccountDto);
        paymentAccountRepository.save(paymentAccount);
        return paymentAccountDto;
    }

    @Transactional
    @Modifying
    @Override
    public PaymentAccountDto updatePaymentAccount(PaymentAccountDto paymentAccountDto) {
        PaymentAccount paymentAccount = paymentAccountMapper.toEntity(paymentAccountDto);
        paymentAccountRepository.deleteById(paymentAccount.getId());
        paymentAccountRepository.save(paymentAccount);
        return paymentAccountDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deletePaymentAccount(Long id) {
        paymentAccountRepository.deleteById(id);
    }
}