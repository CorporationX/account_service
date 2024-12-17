package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.validator.AccountValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AccountService {
    private final AccountValidator accountValidator;
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDtoResponse open(@Valid AccountDtoOpen dto) {
        if (accountRepository.isExistsAccount(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account with number " + dto.getAccountNumber() + " already exists.");
        }
        accountValidator.validateAccountForOpen(dto);

        Owner owner = ownerRepository.findOwnerByIdAndType(dto.getOwnerId(), dto.getOwnerType()).orElse(null);
        if (owner == null) {
            ownerRepository.save(Owner.builder()
                    .ownerId(dto.getOwnerId())
                    .type(dto.getOwnerType())
                    .accounts(new ArrayList<>())
                    .build()
            );
        }
        Account account = accountMapper.toAccount(dto, owner);
        account.setStatus(AccountStatus.PENDING);
        Account savedAccount = accountRepository.save(account);
        owner.getAccounts().add(savedAccount);

        return accountMapper.toDto(savedAccount);
    }
}
