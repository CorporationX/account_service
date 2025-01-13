package faang.school.accountservice.service;

import faang.school.accountservice.dto.Accoun.RequestAccount;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@SpringBootTest
@Testcontainers
@EnableAutoConfiguration(exclude = WebMvcAutoConfiguration.class)
public class FreeAccountNumberServiceTests {

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountRepository freeAccountRepository;

    @MockBean
    private AccountSeqRepository accountSeqRepository;

    @MockBean
    private AccountRepository accountRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void testGenerateAccountNumbers() {
        AccountType accountType = AccountType.DEBIT;
        int batchSize = 5;
        Object[] row1 = new Object[]{"DEBIT", 10L, 5L};
        Object[] row2 = new Object[]{"DEBIT", 20L, 15L};
        List<Object[]> mockResult = Arrays.asList(row1, row2);
        when(accountSeqRepository.incrementCounter(accountType.name(), batchSize)).thenReturn(mockResult);
        freeAccountNumberService.generateAccountNumbers(accountType, batchSize);
        long availableNumbers = freeAccountRepository.count();
        assertEquals(batchSize, availableNumbers);
    }

    @Test
    public void testcreateAccountWithFreeNumber() {
        AccountType accountType = AccountType.DEBIT;
        int batchSize = 5;
        long number = 5536_0000_0000_0000L;
        FreeAccountId freeAccountId = new FreeAccountId(accountType, number);
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);
        RequestAccount requestAccount = new RequestAccount(1L, 2L, AccountType.DEBIT, Owner.USER, Currency.RUB, 3L);
        FreeAccountRepository freeAccountRepository = mock(FreeAccountRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        Consumer<FreeAccountNumber> numberConsumer = mock(Consumer.class);
        when(freeAccountRepository.retrieveFirst(accountType.name())).thenReturn(freeAccountNumber);
        doAnswer(invocation -> {
            FreeAccountNumber arg = invocation.getArgument(0);
            assertEquals(freeAccountNumber, arg);
            return null;
        }).when(numberConsumer).accept(freeAccountNumber);
        FreeAccountNumberService freeAccountNumberService = new FreeAccountNumberService(accountSeqRepository,freeAccountRepository,accountRepository);
        freeAccountNumberService.createAccountWithFreeNumber(requestAccount);
        verify(accountRepository, times(1)).saveAndFlush(any(Account.class));
    }
}