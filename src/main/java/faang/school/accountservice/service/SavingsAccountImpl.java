package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
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
    private final GenerateNumbers generateNumbers;



    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        SavingsAccount account = savingsAccountMapper.toEntity(savingsAccountDto);
        account.getAccount().setNumber(generateNumbers.prepareNumberForAccount());
        savingsAccountRepository.save(account);
        return savingsAccountDto;
    }

    @Override
    public SavingsAccountDto getSavingsAccountById(Long id) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Savings account not found with id " + id));

        return toSavingsAccountDto(savingsAccount);
    }

    @Override
    public SavingsAccountDto getSavingsAccountByUserId(Long userId) {
        SavingsAccount savingsAccount = accountRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Savings account not found with id " + userId)).getSavingsAccount();

        return toSavingsAccountDto(savingsAccount);
    }

    private SavingsAccountDto toSavingsAccountDto(SavingsAccount savingsAccount) {
        Tariff currentTariff = savingsAccount.getTariff();
        List<BigDecimal> tariffHistory = savingsAccount.getTariff().getBettingHistory();
        String number = generateNumbers.prepareNumberForAccount();

        return SavingsAccountDto.builder()
                .accountId(savingsAccount.getId())
                .number(number)
                .tariffId(currentTariff.getId())
                .bettingHistory(tariffHistory)
                .build();
    }


}
