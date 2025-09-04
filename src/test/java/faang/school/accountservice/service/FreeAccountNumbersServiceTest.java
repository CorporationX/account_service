package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import faang.school.accountservice.exception.NoAvailableAccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Transactional
@Sql(scripts = "classpath:test/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FreeAccountNumbersServiceTest extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository sequenceRepository;

    @BeforeEach
    void setUp() {
        for (AccountType accountType : AccountType.values()) {
            if (!sequenceRepository.existsById(accountType)) {
                sequenceRepository.save(new AccountNumbersSequence(accountType));
            }
        }
    }

    @Nested
    @DisplayName("createFreeAccountNumber tests")
    class CreateFreeAccountNumberTests {

        @Test
        @DisplayName("Should create valid free account number")
        void shouldCreateValidFreeAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String accountNumber = "420000000001";

            FreeAccountNumbers result = freeAccountNumbersService.createFreeAccountNumber(accountType, accountNumber);

            assertThat(result).isNotNull();
            assertThat(result.getAccountType()).isEqualTo(accountType);
            assertThat(result.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(result.getCreatedAt()).isNotNull();

            assertThat(freeAccountNumbersRepository.existsById(
                    new faang.school.accountservice.entity.FreeAccountNumberId(accountType, accountNumber)))
                    .isTrue();
        }

        @Test
        @DisplayName("Should throw exception for invalid account number")
        void shouldThrowExceptionForInvalidAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String invalidAccountNumber = "123456";

            assertThatThrownBy(() ->
                    freeAccountNumbersService.createFreeAccountNumber(accountType, invalidAccountNumber))
                    .isInstanceOf(InvalidAccountNumberException.class)
                    .hasMessageContaining("Invalid account number '123456' for account type DEBIT");
        }

        @Test
        @DisplayName("Should throw exception for duplicate account number")
        void shouldThrowExceptionForDuplicateAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String accountNumber = "420000000001";

            freeAccountNumbersService.createFreeAccountNumber(accountType, accountNumber);

            assertThatThrownBy(() ->
                    freeAccountNumbersService.createFreeAccountNumber(accountType, accountNumber))
                    .isInstanceOf(InvalidAccountNumberException.class)
                    .hasMessageContaining("Account number '420000000001' already exists or violates constraints");
        }

        @Test
        @DisplayName("Should handle null account number")
        void shouldHandleNullAccountNumber() {
            AccountType accountType = AccountType.DEBIT;

            assertThatThrownBy(() ->
                    freeAccountNumbersService.createFreeAccountNumber(accountType, null))
                    .isInstanceOf(InvalidAccountNumberException.class);
        }
    }

    @Nested
    @DisplayName("generateFreeAccountNumbers tests")
    class GenerateFreeAccountNumbersTests {

        @Test
        @DisplayName("Should generate requested number of free account numbers")
        void shouldGenerateRequestedNumberOfFreeAccountNumbers() {
            AccountType accountType = AccountType.SAVINGS;
            int count = 5;

            List<FreeAccountNumbers> result = freeAccountNumbersService.generateFreeAccountNumbers(accountType, count);

            assertThat(result).hasSize(count);

            result.forEach(freeNumber -> {
                assertThat(freeNumber.getAccountType()).isEqualTo(accountType);
                assertThat(freeNumber.getAccountNumber()).startsWith(accountType.getPrefix());
                assertThat(freeNumber.getCreatedAt()).isNotNull();
                assertThat(accountType.isValidAccountNumber(freeNumber.getAccountNumber())).isTrue();
            });

            List<String> accountNumbers = result.stream()
                    .map(FreeAccountNumbers::getAccountNumber)
                    .toList();
            assertThat(accountNumbers).doesNotHaveDuplicates();

            long countInDb = freeAccountNumbersRepository.countByAccountType(accountType);
            assertThat(countInDb).isEqualTo(count);
        }

        @Test
        @DisplayName("Should throw exception for zero or negative count")
        void shouldThrowExceptionForZeroOrNegativeCount() {
            AccountType accountType = AccountType.DEBIT;

            assertThatThrownBy(() ->
                    freeAccountNumbersService.generateFreeAccountNumbers(accountType, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Count must be positive: 0");

            assertThatThrownBy(() ->
                    freeAccountNumbersService.generateFreeAccountNumbers(accountType, -5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Count must be positive: -5");
        }

        @Test
        @DisplayName("Should generate sequential account numbers")
        void shouldGenerateSequentialAccountNumbers() {
            AccountType accountType = AccountType.CREDIT;
            int count = 3;

            List<FreeAccountNumbers> result = freeAccountNumbersService.generateFreeAccountNumbers(accountType, count);

            assertThat(result).hasSize(count);

            List<Long> sequences = result.stream()
                    .map(fn -> accountType.extractSequence(fn.getAccountNumber()))
                    .sorted()
                    .toList();

            for (int i = 1; i < sequences.size(); i++) {
                assertThat(sequences.get(i)).isEqualTo(sequences.get(i-1) + 1);
            }
        }

        @Test
        @DisplayName("Should handle large batch generation")
        void shouldHandleLargeBatchGeneration() {
            AccountType accountType = AccountType.BUSINESS;
            int count = 1000;

            List<FreeAccountNumbers> result = freeAccountNumbersService.generateFreeAccountNumbers(accountType, count);

            assertThat(result).hasSize(count);

            List<String> accountNumbers = result.stream()
                    .map(FreeAccountNumbers::getAccountNumber)
                    .toList();

            assertThat(accountNumbers).doesNotHaveDuplicates();
            accountNumbers.forEach(number ->
                    assertThat(accountType.isValidAccountNumber(number)).isTrue());
        }
    }

    @Nested
    @DisplayName("getAndReserveFreeAccountNumber tests")
    class GetAndReserveFreeAccountNumberTests {

        @Test
        @DisplayName("Should retrieve and reserve existing free account number")
        void shouldRetrieveAndReserveExistingFreeAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String expectedAccountNumber = "420000000001";
            freeAccountNumbersService.createFreeAccountNumber(accountType, expectedAccountNumber);

            long initialCount = freeAccountNumbersRepository.countByAccountType(accountType);

            String result = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);

            assertThat(result).isEqualTo(expectedAccountNumber);

            long finalCount = freeAccountNumbersRepository.countByAccountType(accountType);
            assertThat(finalCount).isEqualTo(initialCount - 1);
        }

        @Test
        @DisplayName("Should generate new number when no free numbers available")
        void shouldGenerateNewNumberWhenNoFreeNumbersAvailable() {
            AccountType accountType = AccountType.SAVINGS;

            assertThat(freeAccountNumbersRepository.countByAccountType(accountType)).isZero();

            String result = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);

            assertThat(result).isNotNull();
            assertThat(result).startsWith(accountType.getPrefix());
            assertThat(accountType.isValidAccountNumber(result)).isTrue();

            Long currentSequence = freeAccountNumbersService.getCurrentSequence(accountType);
            assertThat(currentSequence).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should retrieve oldest free account number (FIFO)")
        void shouldRetrieveOldestFreeAccountNumber() {
            AccountType accountType = AccountType.CREDIT;

            freeAccountNumbersService.createFreeAccountNumber(accountType, "550000000001");

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            freeAccountNumbersService.createFreeAccountNumber(accountType, "550000000002");

            String result = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);

            assertThat(result).isEqualTo("550000000001");
        }
    }

    @Nested
    @DisplayName("executeWithFreeAccountNumber tests")
    class ExecuteWithFreeAccountNumberTests {

        @Test
        @DisplayName("Should execute operation with free account number successfully")
        void shouldExecuteOperationWithFreeAccountNumberSuccessfully() {
            AccountType accountType = AccountType.DEBIT;
            String expectedResult = "Operation completed";

            String result = freeAccountNumbersService.executeWithFreeAccountNumber(
                    accountType,
                    accountNumber -> {
                        assertThat(accountNumber).isNotNull();
                        assertThat(accountType.isValidAccountNumber(accountNumber)).isTrue();
                        return expectedResult;
                    }
            );

            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should propagate exception from operation")
        void shouldPropagateExceptionFromOperation() {
            AccountType accountType = AccountType.SAVINGS;
            RuntimeException expectedException = new RuntimeException("Operation failed");

            assertThatThrownBy(() ->
                    freeAccountNumbersService.executeWithFreeAccountNumber(
                            accountType,
                            accountNumber -> { throw expectedException; }
                    ))
                    .isEqualTo(expectedException);
        }
    }

    @Nested
    @DisplayName("ensureMinimumFreeNumbers tests")
    class EnsureMinimumFreeNumbersTests {

        @Test
        @DisplayName("Should generate numbers when below minimum")
        void shouldGenerateNumbersWhenBelowMinimum() {
            AccountType accountType = AccountType.BUSINESS;
            int minCount = 10;

            assertThat(freeAccountNumbersRepository.countByAccountType(accountType)).isLessThan(minCount);

            int generated = freeAccountNumbersService.ensureMinimumFreeNumbers(accountType, minCount);

            assertThat(generated).isEqualTo(minCount);
            assertThat(freeAccountNumbersRepository.countByAccountType(accountType)).isEqualTo(minCount);
        }

        @Test
        @DisplayName("Should not generate numbers when above minimum")
        void shouldNotGenerateNumbersWhenAboveMinimum() {
            AccountType accountType = AccountType.CREDIT;
            int existingCount = 5;
            int minCount = 3;

            freeAccountNumbersService.generateFreeAccountNumbers(accountType, existingCount);

            int generated = freeAccountNumbersService.ensureMinimumFreeNumbers(accountType, minCount);

            assertThat(generated).isZero();
            assertThat(freeAccountNumbersRepository.countByAccountType(accountType)).isEqualTo(existingCount);
        }

        @Test
        @DisplayName("Should throw exception for non-positive minimum count")
        void shouldThrowExceptionForNonPositiveMinimumCount() {
            AccountType accountType = AccountType.DEBIT;

            assertThatThrownBy(() ->
                    freeAccountNumbersService.ensureMinimumFreeNumbers(accountType, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Minimum count must be positive: 0");

            assertThatThrownBy(() ->
                    freeAccountNumbersService.ensureMinimumFreeNumbers(accountType, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Minimum count must be positive: -1");
        }
    }

    @Nested
    @DisplayName("generateFreeNumbersForAllTypes tests")
    class GenerateFreeNumbersForAllTypesTests {

        @Test
        @DisplayName("Should generate free numbers for all account types")
        void shouldGenerateFreeNumbersForAllAccountTypes() {
            int countPerType = 3;

            freeAccountNumbersService.generateFreeNumbersForAllTypes(countPerType);

            for (AccountType accountType : AccountType.values()) {
                long count = freeAccountNumbersRepository.countByAccountType(accountType);
                assertThat(count).isEqualTo(countPerType);
            }
        }

        @Test
        @DisplayName("Should throw exception for non-positive count per type")
        void shouldThrowExceptionForNonPositiveCountPerType() {
            assertThatThrownBy(() ->
                    freeAccountNumbersService.generateFreeNumbersForAllTypes(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Count per type must be positive: 0");
        }
    }

    @Nested
    @DisplayName("Query methods tests")
    class QueryMethodsTests {

        @Test
        @DisplayName("Should count free account numbers correctly")
        void shouldCountFreeAccountNumbersCorrectly() {
            AccountType accountType = AccountType.DEBIT;
            int expectedCount = 7;

            freeAccountNumbersService.generateFreeAccountNumbers(accountType, expectedCount);

            long actualCount = freeAccountNumbersService.countFreeAccountNumbers(accountType);

            assertThat(actualCount).isEqualTo(expectedCount);
        }

        @Test
        @DisplayName("Should check existence of free account numbers correctly")
        void shouldCheckExistenceOfFreeAccountNumbersCorrectly() {
            AccountType accountType = AccountType.SAVINGS;

            assertThat(freeAccountNumbersService.hasFreeAccountNumbers(accountType)).isFalse();

            freeAccountNumbersService.generateFreeAccountNumbers(accountType, 1);
            assertThat(freeAccountNumbersService.hasFreeAccountNumbers(accountType)).isTrue();
        }

        @Test
        @DisplayName("Should get current sequence correctly")
        void shouldGetCurrentSequenceCorrectly() {
            AccountType accountType = AccountType.CREDIT;

            assertThat(freeAccountNumbersService.getCurrentSequence(accountType)).isZero();

            freeAccountNumbersService.generateFreeAccountNumbers(accountType, 5);
            Long currentSequence = freeAccountNumbersService.getCurrentSequence(accountType);
            assertThat(currentSequence).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("validateAccountNumber tests")
    class ValidateAccountNumberTests {

        @Test
        @DisplayName("Should not throw exception for valid account number")
        void shouldNotThrowExceptionForValidAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String validAccountNumber = "420000000001";

            assertThatNoException().isThrownBy(() ->
                    freeAccountNumbersService.validateAccountNumber(accountType, validAccountNumber));
        }

        @Test
        @DisplayName("Should throw exception for invalid account number")
        void shouldThrowExceptionForInvalidAccountNumber() {
            AccountType accountType = AccountType.DEBIT;
            String invalidAccountNumber = "123456";

            assertThatThrownBy(() ->
                    freeAccountNumbersService.validateAccountNumber(accountType, invalidAccountNumber))
                    .isInstanceOf(InvalidAccountNumberException.class)
                    .hasMessageContaining("Invalid account number '123456' for account type DEBIT")
                    .hasMessageContaining("Expected format: 4200 + 8-16 digits");
        }
    }

    @Nested
    @DisplayName("initializeSequence tests")
    class InitializeSequenceTests {

        @Test
        @DisplayName("Should initialize sequence for new account type")
        void shouldInitializeSequenceForNewAccountType() {
            AccountType accountType = AccountType.BUSINESS;
            sequenceRepository.deleteById(accountType);

            AccountNumbersSequence result = freeAccountNumbersService.initializeSequence(accountType);

            assertThat(result).isNotNull();
            assertThat(result.getAccountType()).isEqualTo(accountType);
            assertThat(result.getCurrentSequence()).isZero();
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when sequence already exists")
        void shouldThrowExceptionWhenSequenceAlreadyExists() {
            AccountType accountType = AccountType.DEBIT;

            assertThatThrownBy(() ->
                    freeAccountNumbersService.initializeSequence(accountType))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Sequence already exists for account type: DEBIT");
        }
    }

    @Nested
    @DisplayName("Concurrent access tests")
    class ConcurrentAccessTests {

        @Test
        @DisplayName("Should handle concurrent account number generation")
        void shouldHandleConcurrentAccountNumberGeneration() throws InterruptedException {
            AccountType accountType = AccountType.DEBIT;
            int threadCount = 10;
            int numbersPerThread = 5;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < numbersPerThread; j++) {
                            String accountNumber = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);
                            assertThat(accountNumber).isNotNull();
                            assertThat(accountType.isValidAccountNumber(accountNumber)).isTrue();
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        System.err.println("Thread failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
            executor.shutdown();

            assertThat(successCount.get()).isGreaterThan(0);

            Long finalSequence = freeAccountNumbersService.getCurrentSequence(accountType);
            assertThat(finalSequence).isEqualTo(successCount.get());
        }

        @Test
        @DisplayName("Should handle concurrent free number reservation")
        void shouldHandleConcurrentFreeNumberReservation() throws InterruptedException {
            AccountType accountType = AccountType.SAVINGS;
            int freeNumbersCount = 20;
            int threadCount = 10;

            freeAccountNumbersService.generateFreeAccountNumbers(accountType, freeNumbersCount);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger reservedCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        String accountNumber = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);
                        assertThat(accountNumber).isNotNull();
                        reservedCount.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Reservation failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
            executor.shutdown();

            assertThat(reservedCount.get()).isGreaterThan(0);
            assertThat(reservedCount.get()).isLessThanOrEqualTo(freeNumbersCount);

            long remainingFreeNumbers = freeAccountNumbersService.countFreeAccountNumbers(accountType);
            assertThat(remainingFreeNumbers).isEqualTo(freeNumbersCount - reservedCount.get());
        }
    }

    @Nested
    @DisplayName("Edge cases and error handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle sequence overflow gracefully")
        void shouldHandleSequenceOverflowGracefully() {
            AccountType accountType = AccountType.DEBIT;

            AccountNumbersSequence sequence = sequenceRepository.findById(accountType).orElseThrow();
            long maxSequence = accountType.getMaxSequenceValue();
            sequence.setCurrentSequence(maxSequence - 1);
            sequenceRepository.save(sequence);

            String accountNumber = freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType);
            assertThat(accountNumber).isNotNull();

            assertThatThrownBy(() ->
                    freeAccountNumbersService.getAndReserveFreeAccountNumber(accountType))
                    .isInstanceOf(NoAvailableAccountNumberException.class)
                    .hasMessageContaining("exceeds maximum value");
        }

        @Test
        @DisplayName("Should handle database constraint violations gracefully")
        void shouldHandleDatabaseConstraintViolationsGracefully() {
            AccountType accountType = AccountType.CREDIT;
            String accountNumber = "550000000001";

            FreeAccountNumbers freeNumber = new FreeAccountNumbers(accountType, accountNumber);
            freeAccountNumbersRepository.save(freeNumber);

            assertThatThrownBy(() ->
                    freeAccountNumbersService.createFreeAccountNumber(accountType, accountNumber))
                    .isInstanceOf(InvalidAccountNumberException.class)
                    .hasMessageContaining("already exists or violates constraints");
        }
    }
}