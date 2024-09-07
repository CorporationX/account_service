package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountUtilService accountUtilService;
    private final AccountMapper accountMapper;

    public AccountDto getAccount(Long id) {
        Account account = accountUtilService.getById(id);
        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByNumber(String number) {
        Account account = accountUtilService.getByNumber(number);
        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByOwner(Long ownerId, String ownerType) {
        Account account = accountUtilService.getByOwner(ownerId, ownerType);
        return accountMapper.toDto(account);
    }

    public AccountDto openAccount(OpenAccountDto openAccountDto) {
        Account openAccount = accountUtilService.openAccount(openAccountDto);
        return accountMapper.toDto(openAccount);
    }

    public AccountDto blockAccount(Long id) {
        String status = Status.FROZEN.name();
        Account blockAccount = accountUtilService.changeAccountStatus(id, status);
        return accountMapper.toDto(blockAccount);
    }

    public AccountDto unblockAccount(Long id) {
        String status = Status.ACTIVE.name();
        Account unblockAccount = accountUtilService.changeAccountStatus(id, status);
        return accountMapper.toDto(unblockAccount);
    }

    public AccountDto closeAccount(Long id) {
        String status = Status.CLOSED.name();
        Account closeAccount = accountUtilService.changeAccountStatus(id, status);
        return accountMapper.toDto(closeAccount);
    }
}
