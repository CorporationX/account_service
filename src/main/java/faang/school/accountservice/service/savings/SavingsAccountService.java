package faang.school.accountservice.service.savings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.savingsAccounts.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savingsAccounts.SavingsAccountReadDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final TariffRepository tariffRepository;
    private final ObjectMapper objectMapper;
    private final SavingsAccountRepository savingsAccountRepository;
    private final FreeAccountNumberService freeAccountNumberService;

    @Transactional
    public SavingsAccountReadDto openSavingsAccount(@Valid SavingsAccountCreateDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Аккаунт не существует"));

        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(dto);
        savingsAccount.setAccount(account);

        freeAccountNumberService.executeWithNewAccountNumber(InvoiceType.SAVINGS, savingsAccount::setAccountNumber);

        Tariff currentTariff = tariffRepository.findTariffByTariffName(dto.getTariffName())
                .orElseThrow(() -> new EntityNotFoundException("Тариф с именем " + dto.getTariffName() + " не найден"));

        List<Long> tariffHistory = new ArrayList<>();
        try {
            if (savingsAccount.getTariffHistory() != null) {
                tariffHistory = objectMapper
                        .readValue(savingsAccount.getTariffHistory(), new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка во время парсинга", e);
        }

        tariffHistory.add(currentTariff.getId());

        try {
            savingsAccount.setTariffHistory(objectMapper.writeValueAsString(tariffHistory));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка во время записи тарифа", e);
        }

        savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toReadDto(savingsAccount);
    }

    public SavingsAccountReadDto getSavingsAccount(Long accountId) {
        return savingsAccountMapper.toReadDto(savingsAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Нет счетов с id " + accountId)));
    }

    public SavingsAccountReadDto getSavingsAccountByClientId(Long clientId) {
        return savingsAccountMapper.toReadDto(
                savingsAccountRepository.findByAccount_AuthorId(clientId)
                        .orElseThrow(() -> new EntityNotFoundException("Нет накопительного счета для клиента " + clientId))
        );
    }
}
