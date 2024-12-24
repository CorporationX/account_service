package faang.school.accountservice.service.savings_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.mapper.savings_account_mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsAccountService {
    ObjectMapper objectMapper;
    SavingsAccountMapper savingsAccountMapper;
    SavingsAccountRepository savingsAccountRepository;
    TariffService tariffService;
    AccountService accountService;

    @Transactional
    public SavingsAccountResponseDto create(SavingsAccountRequestDto savingsAccountRequestDto) {
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(savingsAccountRequestDto);
        Account account = accountService.getAccountEntity(savingsAccountRequestDto.getAccountId());
        account.setCreatedAt(OffsetDateTime.now());
        savingsAccount.setAccount(account);
        savingsAccount.setTariff(tariffService.getTariffEntity(savingsAccountRequestDto.getTariffId()));
        addTariffToHistory(savingsAccount, savingsAccountRequestDto);
        SavingsAccount createdAccount = savingsAccountRepository.save(savingsAccount);
        log.info("Savings account created: {}", createdAccount);
        return savingsAccountMapper.toDto(createdAccount);
    }

    public SavingsAccountResponseDto getById(Long savingsAccountId) {
        log.info("Retrieving SavingsAccount with id: {}", savingsAccountId);
        SavingsAccount savingsAccount = savingsAccountRepository.getReferenceById(savingsAccountId);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    public List<SavingsAccountResponseDto> getAllByOwnerId(Long ownerId) {

        return null;
    }

    private void addTariffToHistory(SavingsAccount savingsAccount, SavingsAccountRequestDto savingsAccountRequestDto) {
        try {
            List<TariffType> tariffs = new ArrayList<>();
            if (savingsAccount.getTariffHistory() != null) {
                tariffs = objectMapper.readValue(savingsAccount.getTariffHistory(), new TypeReference<>() {
                });
            }
            tariffs.add(tariffService.getTariffEntity(savingsAccountRequestDto.getTariffId()).getTariffType());
            savingsAccount.setTariffHistory(objectMapper.writeValueAsString(tariffs));
        } catch (JsonProcessingException e) {
            log.error("Json processing error", e);
            throw new RuntimeException("Json processing error", e);
        }
    }
}