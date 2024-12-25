package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.FilterSpecification;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountFilterOwner implements FilterSpecification<Account, AccountDtoFilter> {
    private final static String FIELD_NAME = "owner";
    private final static String FIELD_NAME_OWNER_ID = "ownerId";

    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getOwnerIds() != null && !filter.getOwnerIds().isEmpty();
    }

    @Override
    public Specification<Account> apply(AccountDtoFilter filter) {
        return (root, query, cb) -> root.get(FIELD_NAME).get(FIELD_NAME_OWNER_ID).in(filter.getOwnerIds());
    }
}
