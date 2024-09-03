package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exeption.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUtilService accountUtilService;
    private final AccountMapper accountMapper;

    public AccountDto getAccount(Long id) {
        Account account = accountUtilService.getById(id);
        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByNumber(String number) {
        Account account = accountRepository.findByNumber(number).orElseThrow(() ->
                new NotFoundException("Account with number = " + number + " not found"));
        log.info("Account with number = {} found", number);
        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByOwner(Long ownerId, String ownerType) {
        Account account = accountRepository.findByOwner(ownerId, ownerType).orElseThrow(() ->
                new NotFoundException("Account by owner with ID = " + ownerId +
                        " and type: " + ownerType + " not found"));
        log.info("Account by owner with ID = {} and type: {} found", ownerId, ownerType);
        return accountMapper.toDto(account);
    }

    public AccountDto openAccount(OpenAccountDto openAccountDto) {
        Account account = accountMapper.toEntity(openAccountDto);
        account.setStatus(Status.ACTIVE);
        Account openAccount = accountRepository.save(account);
        log.info("Account with number = {} opened", openAccountDto.getNumber());
        return accountMapper.toDto(openAccount);
    }
}
