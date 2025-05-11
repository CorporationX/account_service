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

        List<Object[]> savingsAccounts = savingsAccountRepository.getSavingsAccountsWithLastTariffRate(numbers);
        if (savingsAccounts.isEmpty()) {
            throw new EntityNotFoundException("Accounts with user id " + userId + " not found");
        }
        return savingsAccounts.stream()
                .map(this::mapToSavingsAccountDto)
                .toList();
    }

    private SavingsAccountDto mapToSavingsAccountDto(Object[] obj) {
        Long id = ((Number) obj[0]).longValue();
        Long tariffId = ((Number) obj[1]).longValue();
        BigDecimal rate = (BigDecimal) obj[2];
        LocalDateTime lastDatePercent = convertObjectToLocalDateTime(obj[3]);
        LocalDateTime createdAt = convertObjectToLocalDateTime(obj[4]);
        LocalDateTime updatedAt = convertObjectToLocalDateTime(obj[5]);

        return SavingsAccountDto.builder()
                .id(id).tariffId(tariffId).rate(rate).lastDatePercent(lastDatePercent)
                .createdAt(createdAt).updatedAt(updatedAt).build();
    }

    private LocalDateTime convertObjectToLocalDateTime(Object obj) {
        if (obj == null) return null;
        LocalDateTime createdAt;
        if (obj instanceof Timestamp) {
            createdAt = ((Timestamp) obj).toLocalDateTime();
        } else if (obj instanceof Instant) {
            createdAt = LocalDateTime.ofInstant((Instant) obj, ZoneId.systemDefault());
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + obj.getClass());
        }
        return createdAt;
    }



}
