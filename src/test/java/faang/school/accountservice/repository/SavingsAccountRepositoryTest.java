package faang.school.accountservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static faang.school.accountservice.util.TestDataFactory.createSavingsAccount;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/test-data-repository/savings-account_and_tariff.sql")
class SavingsAccountRepositoryTest {
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    @Test
    void givenValidAccountNumberWhenFindSavingsAccountByAccountNumberThenReturnSavingsAccount() {
        // given - precondition
        var expectedResult = createSavingsAccount();
        var number = expectedResult.getAccount().getNumber();

        // when - action
        var actualResult = savingsAccountRepository.findSavingsAccountByAccountNumber(number);

        // then - verify the output
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getAccount().getNumber()).isEqualTo(expectedResult.getAccount().getNumber());
    }
}