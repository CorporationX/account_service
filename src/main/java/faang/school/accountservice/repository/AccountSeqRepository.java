package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountSeq;
import faang.school.accountservice.model.account.enums.AccountType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountSeqRepository extends CrudRepository<AccountSeq, String> {
    AccountSeq findByType(AccountType type);

    @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
    Long getNextCounterValue();
}
