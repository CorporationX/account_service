package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.FilterSpecification;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountFilterType implements FilterSpecification<Account, AccountDtoFilter> {
    private final static String FIELD_NAME = "accountType";

    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getAccountTypes() != null && !filter.getAccountTypes().isEmpty();
    }

    @Override
    public Specification<Account> apply(AccountDtoFilter filter) {
        return (root, query, cb) -> root.get(FIELD_NAME).in(filter.getAccountTypes());
    }
}
