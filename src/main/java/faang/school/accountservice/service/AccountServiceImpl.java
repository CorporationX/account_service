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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentAccountMapper paymentAccountMapper;

    @Override
    public PaymentAccountDto getPaymentAccount(Long id) {
        return paymentAccountRepository.findById(id).
                map(paymentAccountMapper::toDto).
                orElseThrow(() -> new EntityNotFoundException("no payment account with id " + id));
    }

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
        PaymentAccount paymentAccount = paymentAccountRepository.findById(paymentAccountDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("no account with id " + paymentAccountDto.getId()));

        paymentAccount.setNumber(paymentAccountDto.getNumber());
        paymentAccount.setAccountType(paymentAccountDto.getAccountType());
        paymentAccount.setCurrency(paymentAccountDto.getCurrency());
        paymentAccount.setUserId(paymentAccountDto.getUserId());
        paymentAccount.setProjectId(paymentAccountDto.getProjectId());
        paymentAccount.setStatus(paymentAccountDto.getStatus());
        paymentAccount.setChangedAt(LocalDateTime.now());
        paymentAccount.setClosedAt(paymentAccountDto.getClosedAt());

        paymentAccountRepository.save(paymentAccount);
        return paymentAccountDto;
    }

    @Override
    public void deletePaymentAccount(Long id) {
        paymentAccountRepository.deleteById(id);
    }
}