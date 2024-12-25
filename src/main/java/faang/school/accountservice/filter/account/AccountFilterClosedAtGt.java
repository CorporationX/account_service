package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.FilterSpecification;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountFilterClosedAtGt implements FilterSpecification<Account, AccountDtoFilter> {
    private final static String FIELD_NAME = "closedAt";

    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getClosedAtGt() != null;
    }

    @Override
    public Specification<Account> apply(AccountDtoFilter filter) {
        return (root, query, cb) -> cb.greaterThan(root.get(FIELD_NAME), filter.getClosedAtGt());
    }
}
