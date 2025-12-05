package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class FreeAccountNumbersServiceTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("testDB")
			.withUsername("test")
			.withPassword("test")
			.withStartupTimeout(Duration.ofMinutes(2))
			.waitingFor(Wait.forListeningPort());

	@Autowired
	private FreeAccountNumbersService freeAccountNumbersService;

	@Autowired
	private FreeAccountNumbersRepository freeAccountRepository;

	@Autowired
	private AccountNumbersSequenceRepository sequenceRepository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		// точные значения из application.yaml
		registry.add("account.number.prefix.personal-checking", () -> "4200000000000000");
		registry.add("account.number.prefix.business-checking", () -> "4300000000000000");
		registry.add("account.number.prefix.savings", () -> "5236000000000000");
		registry.add("account.number.prefix.currency", () -> "5500000000000000");
		registry.add("account.number.prefix.deposit", () -> "6500000000000000");
		registry.add("account.number.batch.size", () -> "10");
	}

	@BeforeEach
	void setUp() {
		freeAccountRepository.deleteAll();
		for (AccountType type : AccountType.values()) {
			AccountNumbersSequence sequence = sequenceRepository.findById(type)
					.orElse(new AccountNumbersSequence());
			sequence.setType(type);
			sequence.setCounter(0);
			sequenceRepository.save(sequence);
		}
	}

	@Test
	void testRetrieveAccountNumber_GeneratesWhenEmpty() {
		List<FreeAccountNumber> captured = new ArrayList<>();
		freeAccountNumbersService.retrieveAccountNumber(AccountType.PERSONAL_CHECKING, captured::add);

		assertEquals(1, captured.size());
		long accountNumber = captured.get(0).getId().getAccountNumber();
		assertTrue(accountNumber >= 4200_0000_0000_0000L);
	}

	@Test
	void testConcurrentAccess() throws InterruptedException {
		int threads = 10;
		CountDownLatch latch = new CountDownLatch(threads);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<FreeAccountNumber> results = new ArrayList<>();

		for (int i = 0; i < threads; i++) {
			executor.submit(() -> {
				freeAccountNumbersService.retrieveAccountNumber(AccountType.BUSINESS_CHECKING, results::add);
				latch.countDown();
			});
		}

		latch.await();
		executor.shutdown();

		assertEquals(threads, results.size());
		assertEquals(threads, results.stream().map(n -> n.getId().getAccountNumber()).distinct().count());
	}

	@Test
	void testGenerateAccountNumber_Batch() {
		freeAccountNumbersService.generateAccountNumber(AccountType.SAVINGS, 5);
		assertEquals(5, freeAccountRepository.count());
	}

	@Test
	void testRetrieveAccountNumber_IncrementsSequenceWhenEmpty() {
		freeAccountNumbersService.retrieveAccountNumber(AccountType.CURRENCY, num -> {
		});

		long counter = sequenceRepository.findById(AccountType.CURRENCY)
				.map(AccountNumbersSequence::getCounter)
				.orElseThrow(() -> new AssertionError("Sequence not found"));

		assertEquals(1, counter);
	}
}