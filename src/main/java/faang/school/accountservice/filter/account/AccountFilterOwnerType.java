package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.filter.FilterSpecification;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountFilterOwnerType implements FilterSpecification<Account, AccountDtoFilter> {
    private final static String FIELD_NAME = "owner";
    private final static String FIELD_NAME_OWNER_TYPE = "type";

    @Override
    public boolean isApplicable(AccountDtoFilter filter) {
        return filter.getOwnerType() != null;
    }

    @Override
    public Specification<Account> apply(AccountDtoFilter filter) {
        return (root, query, cb) -> cb.equal(root.get(FIELD_NAME).get(FIELD_NAME_OWNER_TYPE), filter.getOwnerType());
    }
}
