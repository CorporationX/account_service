package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {

    AccountDto getById(Long id);

    AccountDto getByAccountNumber(String accountNumber);

    AccountDto openAccount(AccountDto accountDto);

    AccountDto blockAccount(String accountNumber);

    AccountDto unblockAccount(String accountNumber);

    AccountDto closeAccount(String accountNumber);

    AccountDto withdraw(String accountNumber, double amount);

    AccountDto deposit(String accountNumber, double amount);

    Double getBalance(String accountNumber);



}