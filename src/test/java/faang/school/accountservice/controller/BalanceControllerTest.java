package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.BalanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(initializers = {BalanceControllerTest.Initializer.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yaml"})
public class BalanceControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final long ACCOUNT_ID = 1L;
    private static final long BALANCE_ID = 1L;
    private BalanceDto balanceDto;
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("BalanceControllerTestDB")
                    .withInitScript("db.changelog/changeset/init-BalanceControllerTest.sql");
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "CONTAINER.USERNAME=" + postgreSQLContainer.getUsername(),
                    "CONTAINER.PASSWORD=", postgreSQLContainer.getPassword(),
                    "CONTAINER.URL=", postgreSQLContainer.getJdbcUrl()
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @BeforeEach
    public void init() {
        balanceDto = new BalanceDto();

        balanceDto.setId(ACCOUNT_ID);
        balanceDto.setAccountId(BALANCE_ID);
        balanceDto.setCurFactBalance(100);
        balanceDto.setCurAuthBalance(100);
        balanceDto.setVersion(1);
    }

    @Test
    void getBalance_whenOk() throws Exception {
        DataSource dataSource = new DriverManagerDataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        );
        JdbcTemplate template = new JdbcTemplate(dataSource);

        List<Account> accounts = template.queryForList("SELECT * FROM account;", Account.class);
        System.out.println(accounts);
//        BalanceDto balance = (BalanceDto) mvc.perform(get("/api/v1/balance/" + ACCOUNT_ID))
//                        .andExpect(status().isOk())
//                        .andReturn().getAsyncResult();
//        Assertions.assertEquals(balanceDto, balance);
    }
}
