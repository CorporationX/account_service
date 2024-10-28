package faang.school.accountservice;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers
public class FreeAccountNumbersServiceIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private FreeAccountNumberRepository freeAccountNumberRepository;

    @BeforeAll
    static void setup() {
        // Опционально: настройка начальных данных
    }

    @AfterEach
    void tearDown() {
        // Сброс данных после каждого теста
        freeAccountNumberRepository.deleteAll();
    }

    @Test
    void testSaveNewAccountNumber() {
        long accountNumber = 1234567890000L;
        FreeAccountNumber newAccountNumber = new FreeAccountNumber();
        newAccountNumber.setAccountNumber(accountNumber);
        newAccountNumber.setAccountType(AccountType.SAVINGS);
        freeAccountNumberRepository.save(newAccountNumber);

        Assertions.assertTrue(freeAccountNumberRepository.findById(1L).isPresent());

        Optional<FreeAccountNumber> freeAccountNumber = freeAccountNumberRepository.findById(1L);
        assertEquals(newAccountNumber, freeAccountNumber.get());

    }

//    @Test
//    void testSaveNewAccountNumbers() {
//        List<Long> accountNumbers = List.of(111111L, 222222L, 333333L);
//        freeAccountNumbersService.saveNewAccountNumbers(accountNumbers);
//
//        Assertions.assertEquals(3, freeAccountNumberRepository.count());
//    }

}
