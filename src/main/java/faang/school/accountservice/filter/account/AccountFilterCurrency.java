package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.FilterSpecification;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountFilterCurrency implements FilterSpecification<Account, AccountDtoFilter> {
    private final static String FIELD_NAME = "currency";

    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getCurrencies() != null && !filter.getCurrencies().isEmpty();
    }

    @Override
    public Specification<Account> apply(AccountDtoFilter filter) {
        return (root, query, cb) -> root.get(FIELD_NAME).in(filter.getCurrencies());
    }
}
