package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final String CODE_MESSAGE_ERROR = "message.error.accountNotFound";
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final MessageSource messageSource;

    public AccountDto getAccount(String number) {
        return mapper.toDto(repository.findAccountByNumber(number)
                .orElseThrow(() -> new RuntimeException((messageSource
                        .getMessage(CODE_MESSAGE_ERROR, null, LocaleContextHolder.getLocale())))));
    }

    public AccountDto openAccount(AccountDto dto) {
        Account entity = mapper.toEntity(dto);
        entity.setNumber("4200000000000001");
        entity.setStatus(Status.ACTIVE);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class)
    public void updateAccountStatus(String number, String status) {
        AccountDto accountDto = getAccount(number);
        Account entity = mapper.toEntity(accountDto);
        entity.setStatus(Status.valueOf(status));
        if (status.equals(Status.CLOSED.toString())) {
            entity.setClosedAt(LocalDateTime.now());
        }
        repository.save(entity);
    }
}