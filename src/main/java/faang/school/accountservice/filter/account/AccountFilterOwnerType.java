package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.model.account.Account;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AccountFilterOwnerType implements Filter<Account, AccountDtoFilter> {
    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getOwnerType() != null;
    }

    @Override
    public Stream<Account> apply(Stream<Account> stream, AccountDtoFilter filter) {
        return stream.filter(account -> account.getOwner().getType().equals(filter.getOwnerType()));
    }
}