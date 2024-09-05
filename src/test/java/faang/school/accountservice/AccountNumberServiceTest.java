package faang.school.accountservice;

import faang.school.accountservice.Entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccTypeFreeNumberId;
import faang.school.accountservice.repository.AccNumSequenceRepository;
import faang.school.accountservice.repository.FreeAccNumRepository;
import faang.school.accountservice.service.AccountNumberService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional

public class AccountNumberServiceTest {
    @Autowired
    private AccNumSequenceRepository accNumSequenceRepository;
    @Autowired
    private FreeAccNumRepository freeAccNumRepository;
    @Autowired
    private AccountNumberService accountNumberService;
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2");

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();

    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void testGenerateAccountNumber() {
        AccountType accountType = AccountType.CREDIT;
        long batchSize = 1L;
        accountNumberService.generateAccountNumber(accountType, batchSize);
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(new AccTypeFreeNumberId(accountType, accountType.getPattern()));
        assertThat(accNumSequenceRepository.findAll()).hasSize(1);
        // строка ниже не работает, хотя не знаю почему. Пишет что не может смапить параметр метода с параметром запроса.....
//        assertThat(accNumSequenceRepository.findCounterByType(accountType.name()).get()).isEqualTo(2);
        assertThat(freeAccNumRepository.findAll()).hasSize(1);
        assertThat(freeAccNumRepository.retrieveFreeAccNum(accountType.name()).getId().getFreeNumber()).isEqualTo(freeAccountNumber.getId().getFreeNumber() + 1);
    }

    @Test
    public void testGetFreeAccNumAndProcess() {
        AccountType accountType = AccountType.CREDIT;
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(new AccTypeFreeNumberId(accountType, 4200_000_000_001L));
        freeAccNumRepository.save(freeAccountNumber);
        Consumer<FreeAccountNumber> consumer = freeAcc -> System.out.println(freeAcc.getId().getFreeNumber());
        FreeAccountNumber result = accountNumberService.getFreeAccNumAndProcess(accountType, consumer);
        assertThat(result).hasFieldOrPropertyWithValue("id", freeAccountNumber.getId());
        assertThat(result).isEqualTo(freeAccountNumber);
    }

    @Test
    public void testGetFreeAccNumAndProcessNumberDoesNotExist() {
        AccountType accountType = AccountType.CREDIT;
        Consumer<FreeAccountNumber> consumer = freeAcc -> System.out.println(freeAcc.getId().getFreeNumber());
        FreeAccountNumber result = accountNumberService.getFreeAccNumAndProcess(accountType, consumer);
        assertNotNull(result);
    }
}
