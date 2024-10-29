package faang.school.accountservice;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class FreeAccountNumbersServiceIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private FreeAccountNumberRepository freeAccountNumberRepository;

    @Autowired
    private AccountSeqRepository accountSeqRepository;

    @BeforeEach
    void tearDown() {
        freeAccountNumberRepository.deleteAll();
    }

    @Test
    void testSaveNewAccountNumber() {
        long accountNumber = 4200_0000_0000_0000L;
        FreeAccountNumber newAccountNumber = new FreeAccountNumber();

        newAccountNumber.setAccountNumber(accountNumber);
        newAccountNumber.setAccountType(AccountType.DEBIT);
        freeAccountNumberRepository.save(newAccountNumber);
        Long id = newAccountNumber.getId();

        Optional<FreeAccountNumber> freeAccountNumber = freeAccountNumberRepository.findById(id);
        assertEquals(newAccountNumber, freeAccountNumber.get());

    }

    @Test
    void testGenerateAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.DEBIT, 100);
    }

}
