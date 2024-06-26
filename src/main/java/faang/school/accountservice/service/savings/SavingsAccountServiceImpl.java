package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.dto.savings.SavingsAccountToCreateDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.validator.savings.SavingsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;
    private final AccountRepository accountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final SavingsValidator savingsValidator;

    @Override
    @Transactional
    public SavingsAccountDto openSavingsAccount(SavingsAccountToCreateDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        Tariff tariff = tariffRepository.findById(dto.getTariffId())
                .orElseThrow(() -> new NotFoundException("Tariff not found"));

        savingsValidator.validateCreate(dto);

        TariffHistory tariffHistory = new TariffHistory();
        tariffHistory.setTariff(tariff);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistory(tariffHistory);
        savingsAccount.setLastInterestCalculationDate(LocalDate.now());
        savingsAccount.setVersion(1L);

        savingsAccountRepository.save(savingsAccount);

        return savingsAccountMapper.toDto(savingsAccount);
    }

    @Override
    public SavingsAccountDto getSavingsAccount(Long id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savings account not found"));

        return savingsAccountMapper.toDto(savingsAccount);
    }
}
