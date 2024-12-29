package faang.school.accountservice.service.account;

import faang.school.accountservice.cache.FreeAccountNumberCache;
import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final FreeAccountNumberCache freeAccountNumberCache;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;

    @Transactional
    public ResponseAccountDto getFreeAccount(RequestAccountDto requestAccountDto, long userId) {
        FreeAccountNumber freeAccountNumber = freeAccountNumberCache.getFreeAccount(requestAccountDto.getAccountType(), requestAccountDto.getCurrency());
        freeAccountNumbersRepository.delete(freeAccountNumber);

        Account account = Account.builder()
                .number(String.valueOf(freeAccountNumber.getId().getAccountNumber()))
                .currency(requestAccountDto.getCurrency())
                .accountType(requestAccountDto.getAccountType())
                .ownerId(userId)
                .ownerType(requestAccountDto.getOwnerType())
                .status(AccountStatus.OPEN)
                .build();

        accountRepository.save(account);

        return mapper.toDto(account);
    }

    private BigInteger generateInitialAccountNumber(AccountType type, Currency currency) {
        String initialNumber = type.getAccountTypeNumber() + String.valueOf(currency.getCurrencyNumber());
        return new BigInteger(initialNumber).multiply(BigInteger.valueOf((long) Math.pow(10, 12)));
    }
}
