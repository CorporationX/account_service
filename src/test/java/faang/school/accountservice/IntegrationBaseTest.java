package faang.school.accountservice;

import faang.school.accountservice.integration.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

@IT
@Sql(scripts = "/sql/data.sql")
public class IntegrationBaseTest {
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @BeforeAll
    static void runContainer(){
        POSTGRE_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    }
}
