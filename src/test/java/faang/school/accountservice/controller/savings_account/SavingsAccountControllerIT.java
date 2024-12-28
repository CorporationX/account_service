package faang.school.accountservice.controller.savings_account;

import faang.school.accountservice.util.BaseContextTest;
import liquibase.exception.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

@Sql("/db/savings_account/create_savings_account_for_test.sql")
@DirtiesContext
public class SavingsAccountControllerIT extends BaseContextTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() throws DatabaseException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tariff", "tariff_rate_changelog", "account_owner", "account");
        jdbcTemplate.execute("SELECT setval('tariff_id_seq', 1, false)");
        jdbcTemplate.execute("SELECT setval('tariff_rate_changelog_id_seq', 1, false)");
    }

    @Test
    void test() {

    }
}
