package faang.school.accountservice.service.account;

import faang.school.accountservice.controller.account.AccountController;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Owner;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.mapper.account.CreateAccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.validator.account.AccountServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CreateAccountMapper createAccountMapper;
    private final AccountServiceValidator validator;

    public List<AccountDto> getAccount(long ownerId, long ownerType) {
        validator.checkId(ownerId, ownerType);

        List<Account> account = getAccountByOwnerIdAndType(ownerId, ownerType);

        return account.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    public AccountDto openNewAccount(CreateAccountDto createAccountDto) {
        Account account = createAccountMapper.toEntity(createAccountDto);
        account.setStatus(Status.ACTIVE);

        accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    public AccountDto changeStatus(long accountId, Status status){
        Account account = getAccountById(accountId);
        account.setStatus(status);

        if(status == Status.CLOSED){
            account.setClosedAt(LocalDateTime.now());
        }

        accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    private Account getAccountById(long accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(()->new EntityNotFoundException(
                        String.format("account with id = %d does not exist",accountId)));
    }

    private List<Account> getAccountByOwnerIdAndType(long ownerId, long ownerType) {
        validator.checkId(ownerId, ownerType);

        Owner owner = Owner.getOwnerById(ownerType);
        return accountRepository.findByOwnerIdAndOwner(ownerId, owner);
    }
}
