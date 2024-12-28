package faang.school.accountservice.controller.savings_account;

import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql("/db/savings_account/create_savings_account_for_test.sql")
public class SavingsAccountControllerIT extends BaseContextTest {

    @Test
    void test() {

    }
}
