package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.client.ProjectDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.common.DataValidationException;
import faang.school.accountservice.exception.common.PreConditionFailedException;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.ProjectInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final ProjectInfoService projectInfoService;
    private final AccountRepository accountRepository;

    public static void validateAccountNotClosed(Account account) {
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.error("Account with id {} already closed", account.getId());
            throw new DataValidationException(String.format("Account with id %s already closed", account.getId()));
        }
    }

    public void validateAccountExistsAndActive(UUID accountId) {
        boolean accountNotAvailable = !accountRepository.existsByIdAndStatus(accountId, AccountStatus.ACTIVE);
        if (accountNotAvailable) {
            log.error("Account with id {} not found!", accountId);
            throw new RecordNotFoundException("Account with id %s not found!".formatted(accountId));
        }
    }

    public void validateAccountCurrency(UUID accountId, Currency currency) {
        boolean wrongCurrency = !accountRepository.existsByIdAndCurrency(accountId, currency);
        if (wrongCurrency) {
            log.error("Account with id {} not suitable for {} transfer!", accountId, currency);
            throw new RecordNotFoundException("Account with id %s and %s currency not found!".formatted(accountId, currency));
        }
    }

    public void validateAccountOwner(Account source, Long userId) {
        boolean operationNotAllowedForUser = true;
        boolean isUserAccount = Objects.isNull(source.getProjectId()) && Objects.nonNull(source.getUserId());
        boolean isProjectAccount = Objects.nonNull(source.getProjectId()) && Objects.isNull(source.getUserId());

        if (isUserAccount) {
            operationNotAllowedForUser = !Objects.equals(source.getUserId(), userId);
        } else if (isProjectAccount) {
            try {
                ProjectDto projectDto = projectInfoService.getProjectInfoById(source.getProjectId());
                operationNotAllowedForUser = !Objects.equals(projectDto.getOwnerId(), userId);
            } catch (Exception e) {
                log.error("Failed to get project information!");
            }
        }
        if (operationNotAllowedForUser) {
            log.error("User {} not the owner of account {}", userId, source.getId());
            throw new PreConditionFailedException("Authorization permitted for user %s".formatted(source.getId()));
        }
    }
}