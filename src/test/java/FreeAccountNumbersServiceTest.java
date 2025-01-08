import faang.school.accountservice.AccountServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = AccountServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
//@ActiveProfiles("integrationtest")
@ComponentScan(basePackages = {"faang.school.accountservice"})
class FreeAccountNumbersServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"))
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpassword");

    @Test
    void testContainerStartup() {
        assertTrue(postgreSQLContainer.isRunning());
    }
}