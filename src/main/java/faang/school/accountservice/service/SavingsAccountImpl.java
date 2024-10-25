package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.dto.TariffHistoryDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        SavingsAccount account = savingsAccountMapper.toEntity(savingsAccountDto);
        account.setNumber(prepareNumberForSavingsAccount());
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
        List<TariffHistory> tariffHistory = savingsAccount.getTariffHistory();
        String number = prepareNumberForSavingsAccount();

        List<TariffHistoryDto> tariffHistoryDtoList = tariffHistory.stream()
                .map(tariff -> new TariffHistoryDto(tariff.getTariff().getId(),
                        tariff.getSavingsAccount().getId()))
                .toList();

        return SavingsAccountDto.builder()
                .accountId(savingsAccount.getId())
                .number(number)
                .tariffId(currentTariff.getId())
                .tariffHistoryDto(tariffHistoryDtoList)
                .build();
    }

    @Scheduled(fixedRate = 3600000)
    private void generateNumbers() {
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numbers.add(freeAccountNumbersService.generateNumberByType(AccountType.SAVINGS_ACCOUNT));
        }

        freeAccountNumbersService.saveNumbers(AccountType.SAVINGS_ACCOUNT, numbers);
    }

    private String prepareNumberForSavingsAccount() {
        return freeAccountNumbersRepository.getFreeAccountNumberByType("SAVINGS_ACCOUNT").orElseThrow(
                () -> new EntityNotFoundException("Number not found"));
    }
}
