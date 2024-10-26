package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.model.Account;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AccountCurrencyFilter implements Filter<AccountFilterDto, Account> {

    @Override
    public boolean isApplicable(AccountFilterDto filters) {
        return filters.currency() != null;
    }

    @Override
    public Stream<Account> apply(Stream<Account> stream, AccountFilterDto filters) {
        return stream.filter(account -> account.getCurrency().equals(filters.currency()));
    }
}
