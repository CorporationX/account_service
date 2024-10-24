package faang.school.accountservice.service;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.dto.TariffHistoryDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;

    @Override
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        savingsAccountRepository.save(savingsAccountMapper.toEntity(savingsAccountDto));
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

        List<TariffHistoryDto> tariffHistoryDtoList = tariffHistory.stream()
                .map(tariff -> new TariffHistoryDto(tariff.getTariff().getId(),
                        tariff.getSavingsAccount().getId()))
                .toList();

        return SavingsAccountDto.builder()
                .accountId(savingsAccount.getId())
                .tariffId(currentTariff.getId())
                .tariffHistoryDto(tariffHistoryDtoList)
                .build();
    }
}
