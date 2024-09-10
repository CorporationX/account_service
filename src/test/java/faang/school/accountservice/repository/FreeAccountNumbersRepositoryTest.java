package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@Sql("/test-data-repository/free-account-numbers.sql")
class FreeAccountNumbersRepositoryTest {
    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Test
    void givenAccountTypeWhenGetSavingsAccountNumberThenReturnSavingsAccountNumber() {
        // given - precondition
        var accountType = AccountType.SAVINGSACCOUNT;
        var expectedResult = "8800 0000 0000 0001";

        // when - action
        var actualResult = freeAccountNumbersRepository.getSavingsAccountNumber(accountType);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void givenAccountNumberWhenDeleteSavingsAccountNumberThenNumberIsDeleted(){
        // given - precondition
        var savingsAccountNumber = "8800 0000 0000 0001";

        // when - action
        freeAccountNumbersRepository.deleteSavingsAccountNumber(savingsAccountNumber);

        // then - verify the output
        assertThat(freeAccountNumbersRepository.findByNumber(savingsAccountNumber).isPresent())
                .isFalse();
    }
}