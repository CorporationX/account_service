package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.mapper.TariffRateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final TariffRateMapper tariffRateMapper;

    public TariffAndRateDto getTariffAndRateByAccountId(Long accountId) {
        return savingsAccountRepository.findById(accountId)
                .map(tariffRateMapper::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("Savings account is not found."));
    }

    public TariffAndRateDto getAccountByClientId(String number) {
        return savingsAccountRepository.findSavingsAccountByAccountNumber(number)
                .map(tariffRateMapper::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("Savings account is not found."));
    }

    public TariffAndRateDto openAccount(SavingsAccountDto savingsAccountDto) {
        String accountNumber =  savingsAccountDto.accountNumber();
        accountRepository.findAccountByNumber(accountNumber);
    }
}
