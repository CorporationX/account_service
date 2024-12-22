package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.model.account.Account;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AccountFilterCreatedAt implements Filter<Account, AccountDtoFilter> {
    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getCreatedAtStart() != null || filter.getCreatedAtEnd() != null;
    }

    @Override
    public Stream<Account> apply(Stream<Account> stream, AccountDtoFilter filter) {
        if (filter.getCreatedAtEnd() == null) {
            filter.setCreatedAtEnd(LocalDateTime.now());
        }
        if (filter.getCreatedAtStart() == null) {
            filter.setCreatedAtStart(LocalDateTime.MIN);
        }

        return stream.filter(account ->
                !account.getCreatedAt().isBefore(filter.getCreatedAtStart()) &&
                !account.getCreatedAt().isAfter(filter.getCreatedAtEnd())
        );
    }
}
