package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.model.account.Account;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AccountFilterClosedAt implements Filter<Account, AccountDtoFilter> {
    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getClosedAtStart() != null || filter.getClosedAtEnd() != null;
    }

    @Override
    public Stream<Account> apply(Stream<Account> stream, AccountDtoFilter filter) {
        if (filter.getClosedAtEnd() == null) {
            filter.setClosedAtEnd(LocalDateTime.now());
        }
        if (filter.getClosedAtStart() == null) {
            filter.setClosedAtStart(LocalDateTime.MIN);
        }
        return stream.filter(account ->
                !account.getClosedAt().isBefore(filter.getClosedAtStart()) &&
                        !account.getCreatedAt().isAfter(filter.getClosedAtEnd())
        );
    }
}
