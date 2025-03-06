package faang.school.accountservice.specification;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;

public class AccountSpecification {

    public static Specification<Account> hasNumber(String number) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("accountNumber"), number);
    }

    public static Specification<Account> hasOwner(BigInteger ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Account> hasType(AccountType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Account> hasStatus(AccountStatus accountStatus) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("accountStatus"), accountStatus);
    }
}
