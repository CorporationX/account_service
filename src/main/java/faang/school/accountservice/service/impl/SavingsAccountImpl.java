package faang.school.accountservice.service.impl;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.NumberGenerator;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class SavingsAccountImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final NumberGenerator numberGenerator;

    @Override
    public SavingsAccountDto createSavingsAccount(SavingsAccountDto savingsAccountDto) {
        SavingsAccount account = savingsAccountMapper.toEntity(savingsAccountDto);
        account.getAccount().setNumber(numberGenerator.prepareNumberForAccount());
        savingsAccountRepository.save(account);
        return savingsAccountDto;
    }

    @Override
    public SavingsAccountDto getSavingsAccountById(Long id) {
        return toSavingsAccountDto(savingsAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Savings account with id %s not found".formatted(id))));
    }

    @Override
    public SavingsAccountDto getSavingsAccountByUserId(Long userId) {
        return toSavingsAccountDto(accountRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Savings account with id %s not found".formatted(userId))).getSavingsAccount());
    }

    private SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount) {
        Tariff currentTariff = savingsAccount.getTariff();
        List<BigDecimal> tariffHistory = savingsAccount.getTariff().getBettingHistory();
        String number = numberGenerator.prepareNumberForAccount();

        return SavingsAccountDto.builder()
                .accountId(savingsAccount.getId())
                .number(number)
                .tariffId(currentTariff.getId())
                .bettingHistory(tariffHistory)
                .build();
    }
}
