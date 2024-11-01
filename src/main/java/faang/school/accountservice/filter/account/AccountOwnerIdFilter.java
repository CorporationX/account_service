package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountOwnerIdFilter implements Filter<AccountFilterDto, Account> {

    @Override
    public boolean isApplicable(AccountFilterDto filters) {
        return filters.ownerId() != null;
    }

    @Override
    public Specification<Account> apply(AccountFilterDto filters) {
        return ((root, query, builder) -> builder.equal(root.get("owner"), filters.ownerId()));
    }
}
