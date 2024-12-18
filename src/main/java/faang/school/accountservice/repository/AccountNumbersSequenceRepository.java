package faang.school.accountservice.repository;

import faang.school.accountservice.entiry.AccountNumberSequence;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository {

    //create increment

    AccountNumberSequence getByAccountType(String accountType);
}