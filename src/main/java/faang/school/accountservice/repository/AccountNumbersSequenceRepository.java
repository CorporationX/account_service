package faang.school.accountservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository {

    void createAccountNumbersSequenceByAccountType(String accountType);
    boolean incrementAccountNumbersSequenceByAccountType(String accountType); // ????
}