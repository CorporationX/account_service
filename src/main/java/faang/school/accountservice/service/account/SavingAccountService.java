package faang.school.accountservice.service.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.HistoryDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountCreateDto;
import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountFilter;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.account.SavingAccountMapper;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingAccountService {
    private final SavingAccountRepository savingAccountRepository;
    private final SavingAccountPaymentService savingAccountPaymentService;
    private final SavingAccountMapper savingAccountMapper;
    private final AccountService accountService;
    private final TariffService tariffService;
    private final ObjectMapper objectMapper;

    @Value("${task.savings-account.batch-size}")
    private int batchSize;
    @Value("${task.savings-account.payment-interval}")
    private int paymentIntervalInDays;

    public SavingAccountDto findById(Long id) {
        return savingAccountMapper.toDto(findEntityById(id));
    }

    public SavingAccount findEntityById(Long id) {
        return savingAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Saving account doesn't exist"));
    }

    public List<SavingAccountDto> findBy(SavingAccountFilter filter) {
        if (filter.getProjectId() == null && filter.getUserId() == null) {
            throw new IllegalArgumentException("ProjectId or UserId is required");
        }
        if (filter.getProjectId() != null && filter.getUserId() != null) {
            throw new IllegalArgumentException("Only ProjectId or only UserId is required");
        }

        return filter.getUserId() != null ?
                findByUserOwner(filter.getUserId()) :
                findByProjectOwner(filter.getProjectId());
    }

    @Transactional
    public SavingAccountDto openAccount(SavingAccountCreateDto requestDto) {
        AccountDto openedAccount = accountService.openAccount(requestDto.getAccount());
        Account account = accountService.getAccount(openedAccount.getId());
        Tariff tariff = tariffService.findEntityById(requestDto.getTariffId());

        SavingAccount savingAccount = SavingAccount.builder()
                .account(account)
                .tariff(tariff)
                .tariffHistory(createTariffHistory(tariff.getId()))
                .build();

        savingAccount = savingAccountRepository.save(savingAccount);
        log.info("Opened saving account - {}, tariff - {}, rate - {}", savingAccount.getAccount().getPaymentNumber(),
                savingAccount.getTariff().getName(), savingAccount.getTariff().getRate());
        return savingAccountMapper.toDto(savingAccount);
    }

    @Transactional
    public void payOffInterests() {
        List<SavingAccount> accounts = savingAccountRepository.findAllForPayment(paymentIntervalInDays);
        log.info("Interest payments of saving accounts started: {}", LocalDateTime.now());
        for (int i = 0; i < accounts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, accounts.size());
            List<SavingAccount> batch = accounts.subList(i, end);
            savingAccountPaymentService.payOffInterests(batch);
        }
    }

    private List<SavingAccountDto> findByUserOwner(Long userId) {
        return savingAccountMapper.toDto(savingAccountRepository.findByAccountOwnerUserId(userId));
    }

    private List<SavingAccountDto> findByProjectOwner(Long projectId) {
        return savingAccountMapper.toDto(savingAccountRepository.findByAccountOwnerProjectId(projectId));
    }

    private String createTariffHistory(Long tariffId) {
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
