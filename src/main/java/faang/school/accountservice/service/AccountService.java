package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountCreationDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;

    public AccountDto getAccountDto(Long id) {
        return mapper.toDto(getAccountById(id));
    }

    public Account getAccountById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
    }

    public Account getAccountByNumber(String number) {
        return repository.findByNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("Number " + number + " not found"));
    }

    public Account getAccountBuOwnerIdAndAccount(Long ownerId, Currency currency){
        return repository.findByOwnerIdAndCurrency(ownerId, currency)
                .orElseThrow(()-> new IllegalArgumentException("Owner id " + ownerId + "is not correct"));
    }

    @Transactional
    public AccountDto openAccount(AccountCreationDto dto) {
        Account newAccount = mapper.toCreateEntity(dto);
        newAccount.setStatus(Status.ACTIVE); // id and number will be auto generated
        Account savedAccount = repository.save(newAccount);
        return mapper.toDto(savedAccount);

    }

    @Transactional
    public AccountDto blockAccount(Long id) {
        Account account = getAccountById(id);
        account.setStatus(Status.FROZEN);
        repository.save(account);
        return mapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(Long id) {
        Account account = getAccountById(id);
        account.setStatus(Status.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        repository.save(account);
        return mapper.toDto(account);
    }
}
