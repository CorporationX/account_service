package faang.school.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.HistoryDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountFilter;
import faang.school.accountservice.dto.account.saving.SavingAccountRequestDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.account.SavingAccountMapper;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingAccountService {
    private final SavingAccountRepository savingAccountRepository;
    private final SavingAccountMapper savingAccountMapper;
    private final AccountService accountService;
    private final TariffService tariffService;
    private final ObjectMapper objectMapper;

    public SavingAccountDto findById(Long id) {
        return savingAccountMapper.toDto(findEntityById(id));
    }

    public SavingAccount findEntityById(Long id) {
        return savingAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Saving account doesn't exist"));
    }

    public List<SavingAccountDto> findBy(SavingAccountFilter filter) {
        if (filter.getProjectId() == null && filter.getUserId() == null) {
            throw new IllegalArgumentException();
        }
        if (filter.getProjectId() != null && filter.getUserId() != null) {
            throw new IllegalArgumentException();
        }

        return filter.getUserId() != null ?
                findByUserOwner(filter.getUserId()) :
                findByProjectOwner(filter.getProjectId());
    }

    public List<SavingAccountDto> findByUserOwner(Long userId) {
        return savingAccountMapper.toDto(savingAccountRepository.findByAccountOwnerUserId(userId));
    }

    public List<SavingAccountDto> findByProjectOwner(Long projectId) {
        return savingAccountMapper.toDto(savingAccountRepository.findByAccountOwnerProjectId(projectId));
    }

    @Transactional
    public SavingAccountDto openAccount(SavingAccountRequestDto requestDto) {
        AccountDto openedAccount = accountService.openAccount(requestDto.getAccount());
        Account account = accountService.getAccount(openedAccount.getId());
        Tariff tariff = tariffService.findEntityById(requestDto.getTariffId());

        SavingAccount savingAccount = SavingAccount.builder()
                .account(account)
                .tariff(tariff)
                .tariffHistory(createTariffHistroy(tariff.getId()))
                .build();

        savingAccount = savingAccountRepository.save(savingAccount);
        log.info("Opened saving account - {}, tariff - {}, rate - {}", savingAccount.getAccount().getPaymentNumber(),
                savingAccount.getTariff().getName(), savingAccount.getTariff().getRate());
        return savingAccountMapper.toDto(savingAccount);
    }


    private String createTariffHistroy(Long tariffId) {
        try {
            List<HistoryDto> history = List.of(new HistoryDto(null, tariffId.toString(), LocalDateTime.now()));
            return objectMapper.writeValueAsString(history);
        } catch (JsonProcessingException e) {
            log.error("Tariff history creation error", e);
            throw new IllegalStateException("Tariff history creation error", e);
        }
    }

    private String updateTariffHistory(String history, Long newTariffId, Long previousTariffId) {
        try {
            List<HistoryDto> tariffHistory = Arrays.asList(objectMapper.readValue(history, HistoryDto[].class));
            tariffHistory.add(new HistoryDto(previousTariffId.toString(), newTariffId.toString(), LocalDateTime.now()));
            return objectMapper.writeValueAsString(tariffHistory);
        } catch (JsonProcessingException e) {
            log.error("Tariff history update error", e);
            throw new IllegalStateException("Tariff history update error", e);
        }
    }

}
