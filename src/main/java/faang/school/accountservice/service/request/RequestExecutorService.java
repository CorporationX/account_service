package faang.school.accountservice.service.request;

import faang.school.accountservice.config.executor.ExecutorServiceConfig;
import faang.school.accountservice.config.kafka.KafkaProperties;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestExecutorService {

    private final ExecutorServiceConfig executorConfig;
    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AccountService accountService;
    private final OwnerService ownerService;

    public void openingAccount(AccountCreateDto accountCreateDto) {
        try {
            CompletableFuture
                    .supplyAsync(()-> checkMaxAmountAccounts(accountCreateDto)
                                    , executorConfig.executorServiceAsync())
                    .thenComposeAsync(maxAccountsReached -> {
                        if (checkMaxAmountAccounts(accountCreateDto)) {
                            return CompletableFuture.supplyAsync(
                                    () -> createAccount(accountCreateDto), executorConfig.executorServiceAsync());
                        } else {
                            return CompletableFuture.failedFuture(
                                    new RuntimeException("Max accounts limit reached for owner."));
                        }
                    }, executorConfig.executorServiceAsync())
                    .thenAcceptAsync(accountDto -> sendNotification(accountDto.getId()),
                            executorConfig.executorServiceAsync())
                    .exceptionally(ex -> {
                        log.error("Request error {}", ex, ex.getCause());
                        return null;
                    });
        } catch (Exception e) {
            rollBack(accountCreateDto);
        }
    }

    private AccountDto createAccount(AccountCreateDto accountCreateDto) {
        try {
            return accountService.createAccount(accountCreateDto);
        } catch (Exception e) {
            log.error("Error during account creation for account ID {}: {}", accountCreateDto.getId(), e.getMessage());
            throw new RuntimeException("Failed to create account", e);
        }
    }

    private boolean checkMaxAmountAccounts(AccountCreateDto accountCreateDto) {
        try {
            int maxAmountBalance = 10;
            return ownerService.getCountOwnerAccounts(
                    ownerService.getCountOwnerAccounts(accountCreateDto.getId())) < maxAmountBalance;
        } catch (Exception e) {
            log.error("Error checking max account limit: ", e);
            throw new RuntimeException("Error checking max account limit", e);
        }
    }

    private void sendNotification(Long accountId) {
        String message = String.format("Account with ID %s has been successfully opened.", accountId);
        sendMessage(accountId.toString(), message);
        log.info("Notification sent for account opening: {}", message);
    }

    private void sendMessage(String key, String value) {
        kafkaTemplate.send(kafkaProperties.getAccountOpeningTopic(), key, value);
        log.info("Message sent: Key = {}, Value = {}", key, value);
    }

    private void rollBack(AccountCreateDto accountCreateDto) {
        log.info("Rollback initiated for account creation with ID: {}", accountCreateDto.getId());

        try {
            Account account = accountService.getAccountForAsync(accountCreateDto.getId());
            if (account != null) {
                log.debug("Account and associated balance deleted for account ID: {}", account.getId());
                accountService.deleteAccount(account);
                log.debug("Account record deleted: {}", account.getId());
            }
        } catch (Exception e) {
            log.error("Error during rollback: ", e);
        }
    }
}
