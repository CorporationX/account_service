package faang.school.accountservice.facade.account;

import faang.school.accountservice.dto.account.AccountCreateProjectRequestDto;
import faang.school.accountservice.dto.account.AccountCreateUserRequestDto;
import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountFacade {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(UUID accountId) {
        Account account = accountService.getAccountById(accountId);

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponseDto(account);
        log.info("Mapping Account entity to AccountResponseDto. Entity content: {}. DTO content: {}.",
                account, accountResponseDto);

        return accountResponseDto;
    }

    @Transactional
    public AccountResponseDto createAccountForUser(AccountCreateUserRequestDto accountCreateUserRequestDto) {
        Account account = accountMapper.toAccountEntity(accountCreateUserRequestDto);
        log.info("Mapping AccountCreateUserRequestDto to Account entity. DTO content: {}. Entity content: {}.",
                accountCreateUserRequestDto, account);

        Account savedAccount = accountService.createAccount(account, accountCreateUserRequestDto.getCurrencyId());

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponseDto(account);
        log.info("Mapping Account entity to AccountResponseDto. Entity content: {}. DTO content: {}.",
                savedAccount, accountResponseDto);

        return accountResponseDto;
    }

    @Transactional
    public AccountResponseDto createAccountForProject(AccountCreateProjectRequestDto accountCreateProjectRequestDto) {
        Account account = accountMapper.toAccountEntity(accountCreateProjectRequestDto);
        log.info("Mapping AccountCreateProjectRequestDto to Account entity. DTO content: {}. Entity content: {}.",
                accountCreateProjectRequestDto, account);

        Account savedAccount = accountService.createAccount(account, accountCreateProjectRequestDto.getCurrencyId());

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponseDto(account);
        log.info("Mapping Account entity to AccountResponseDto. Entity content: {}. DTO content: {}.",
                savedAccount, accountResponseDto);

        return accountResponseDto;
    }
}
