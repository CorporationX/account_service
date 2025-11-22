package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.model.OwnerType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountDto getById(Long id);

    AccountDto getByAccountNumber(String accountNumber);

    AccountDto openAccount(CreateAccountDto accountDto);

    AccountDto blockAccount(String accountNumber);

    AccountDto unblockAccount(String accountNumber);

    AccountDto closeAccount(String accountNumber);

    AccountDto withdraw(String accountNumber, BigDecimal amount);

    AccountDto deposit(String accountNumber, BigDecimal amount);

    BigDecimal getBalance(String accountNumber);

    List<AccountDto> getAccountsByOwner(Long ownerId, OwnerType ownerType);



}