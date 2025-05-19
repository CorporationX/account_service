package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountService {
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;
    private final TariffHistoryRepository tariffHistoryRepository;

    @Transactional
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        Tariff tariff = tariffRepository.findById(savingsAccountDto.getTariffId())
                .orElseThrow(() -> new EntityNotFoundException("Tariff with id " + savingsAccountDto.getTariffId() + " not found"));

        Account account = accountRepository.findById(savingsAccountDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + savingsAccountDto.getAccountId() + " not found"));

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .build();
        savingsAccount = savingsAccountRepository.save(savingsAccount);

        TariffHistory tariffHistory = TariffHistory.builder()
                .savingsAccount(savingsAccount)
                .tariff(tariff)
                .build();
        tariffHistoryRepository.save(tariffHistory);
        SavingsAccountDto resultDto = savingsAccountMapper.toSavingsAccountDto(savingsAccount);
        resultDto.setTariffId(tariff.getId());
        return resultDto;
    }

    public SavingsAccountDto getSavingsAccount(Long id) {
        Optional<SavingsAccountDto> savingsAccountDto = savingsAccountRepository.findSavingsAccountWithDetails(id);
        return savingsAccountDto
                .orElseThrow(() -> new EntityNotFoundException("SavingsAccount with id " + id + " not found"));
    }

    public List<SavingsAccountDto> getSavingsAccountByUserId(Long userId) {
        List<String> numbers = accountRepository.findNumbersByOwnerId(userId);
        if (numbers.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user id " + userId + " not found");
        }

        List<SavingsAccount> savingsAccounts = savingsAccountRepository.getSavingsAccountsWithLastTariffRate(numbers);
        if (savingsAccounts.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user id " + userId + " not found");
        }
        return savingsAccounts.stream()
                .map(this::mapToSavingsAccountDto)
                .toList();
    }

    private SavingsAccountDto mapToSavingsAccountDto(SavingsAccount account) {
        Long id = account.getId();
        LocalDateTime lastDatePercent = account.getLastDatePercent();
        LocalDateTime createdAt = account.getCreatedAt();
        LocalDateTime updatedAt = account.getUpdatedAt();

        return SavingsAccountDto.builder()
                .id(id).lastDatePercent(lastDatePercent)
                .createdAt(createdAt).updatedAt(updatedAt).build();
    }
}
