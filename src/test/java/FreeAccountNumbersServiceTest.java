import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.services.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AccountServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class FreeAccountNumbersServiceTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @DynamicPropertySource
    static void setDatasourceProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        freeAccountNumbersRepository = mock(FreeAccountNumbersRepository.class);
        accountNumbersSequenceRepository = mock(AccountNumbersSequenceRepository.class);
        freeAccountNumbersService = new FreeAccountNumbersService(freeAccountNumbersRepository, accountNumbersSequenceRepository);
    }

    @Test
    void testContainerStartup() {
        assertTrue(postgresContainer.isRunning());
    }

    @Test
    void createNewFreeAccountNumber_ShouldCreateNumber() {
        String accountType = "SAVINGS";
        Integer fourDigits = 1234;
        Long lastDigits = 1L;
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrent(lastDigits);

        when(accountNumbersSequenceRepository.findByAccountType(accountType)).thenReturn(sequence);
        doNothing().when(accountNumbersSequenceRepository).incrementCounter(accountType, lastDigits);

        freeAccountNumbersService.createNewFreeAccountNumber(accountType);

        verify(freeAccountNumbersRepository)
                .saveNewFreeAccountNumber(eq(accountType), anyLong());
        verify(accountNumbersSequenceRepository).incrementCounter(accountType, lastDigits);
    }

    @Test
    void createNewFreeAccountNumber_ShouldThrowException_WhenSequenceNotFound() {
        String accountType = "INVALID";
        when(accountNumbersSequenceRepository.findByAccountType(accountType)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewFreeAccountNumber(accountType));

        assertEquals("Invalid account type: " + accountType, exception.getMessage());
    }

    @Test
    void createNewAccountNumbersSequence_ShouldCreateSequence_WhenNotExists() {
        String accountType = "CHECKING";

        when(accountNumbersSequenceRepository.findByAccountType(accountType)).thenReturn(null);
        doNothing().when(accountNumbersSequenceRepository).createAccountTypeCounter(accountType);

        freeAccountNumbersService.createNewAccountNumbersSequence(accountType);

        verify(accountNumbersSequenceRepository, times(1)).createAccountTypeCounter(accountType);
    }

    @Test
    void createNewAccountNumbersSequence_ShouldThrowException_WhenExists() {
        String accountType = "CHECKING";
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrent(0L);
        when(accountNumbersSequenceRepository.findByAccountType(accountType)).thenReturn(sequence);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewAccountNumbersSequence(accountType));

        assertEquals("AccountNumbersSequence for account type " + accountType + " is exist", exception.getMessage());
    }

    @Test
    void getFreeAccountNumberWithTransaction_ShouldCreateAndRetrieveNumber_WhenNotExists() {
        String accountType = "SAVINGS";
        Long newAccountNumber = 1234000000001L;

        when(freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType)).thenReturn(null, newAccountNumber);
        doNothing().when(freeAccountNumbersRepository).saveNewFreeAccountNumber(eq(accountType), anyLong());
        doNothing().when(accountNumbersSequenceRepository).incrementCounter(eq(accountType), anyLong());

        freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, () -> {
        });

        verify(freeAccountNumbersRepository, times(2)).getAndRemoveFirstFreeAccountNumber(accountType);
    }
}
