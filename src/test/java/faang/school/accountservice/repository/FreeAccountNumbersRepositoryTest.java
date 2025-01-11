package faang.school.accountservice.repository;

import faang.school.accountservice.util.BaseContextTest;
import jakarta.transaction.Transactional;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class FreeAccountNumbersRepositoryTest extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;


    @BeforeAll
    static void setup() {
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRESQL_CONTAINER.getJdbcUrl(), POSTGRESQL_CONTAINER.getUsername(), POSTGRESQL_CONTAINER.getPassword())
                .load();
        flyway.migrate();
    }

    @Test
    void saveNewFreeAccountNumberSuccessTest() {
        String accountType = "SAVING";
        Long accountNumber = 523600000000001L;

        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber);

        Long retrievedAccountNumber = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertNotNull(retrievedAccountNumber);
        assertEquals(accountNumber, retrievedAccountNumber);
    }

    @Test
    void getAndRemoveFreeAccountNumbersSuccessTest() {
        String accountType = "SAVING";
        Long accountNumber1 = 523600000000001L;
        Long accountNumber2 = 523600000000002L;
        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber1);
        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber2);

        Long firstAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        Long secondAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertEquals(accountNumber1, firstAccount);
        assertEquals(accountNumber2, secondAccount);
    }

    @Test
    void getAndRemoveFreeAccountNumberWithWrongAccountTypeSuccessTest() {
        String accountType = "INVALID";
        Long firstAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertNull(firstAccount);
    }
}