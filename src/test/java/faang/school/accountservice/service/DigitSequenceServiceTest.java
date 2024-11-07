package faang.school.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class DigitSequenceServiceTest {
//    @Mock
//    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
//
//    @Mock
//    private FreeAccountNumbersRepository freeAccountNumbersRepository;
//
//    @Mock
//    private AccountNumberConfig accountNumberConfig;
//
//    @InjectMocks
//    private DigitSequenceService digitSequenceService;
//
//    private AccountNumberType type;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        type = AccountNumberType.BUSINESS; // Установка типа счета
//    }
//
//    @Test
//    void testCheckForGenerationSequencesAsync_whenCounterIsAbsent() {
//        when(accountNumbersSequenceRepository.tryLockCounterByTypeForUpdate(type.toString())).thenReturn(Optional.empty());
//
//        digitSequenceService.checkForGenerationSequencesAsync(type);
//
//        //verify(accountNumbersSequenceRepository, never()).setActiveGenerationState(anyString());
//    }
//
//    @Test
//    void testTryActivateGenerationState_whenGenerationIsInactive() {
//        AccountUniqueNumberCounter counter = new AccountUniqueNumberCounter();
//    }
//
//
//    @Test
//    void testGetAndRemoveFreeAccountNumberByType_whenAccountNumberExists() {
//        String accountNumber = "BUS-0000001";
//
//        Optional<String> result = digitSequenceService.getAndRemoveFreeAccountNumberByType(type);
//
//        assertTrue(result.isPresent());
//        assertEquals(accountNumber, result.get());
//    }
//
//    @Test
//    void testGenerateNewAccountNumberWithoutPool() {
//        when(accountNumbersSequenceRepository.incrementAndGet(type.toString())).thenReturn(1L);
//        when(accountNumberConfig.getMaxlengthOfDigitSequence()).thenReturn(20);
//
//        String accountNumber = digitSequenceService.generateNewAccountNumberWithoutPool(type);
//
//        assertEquals("42600000000000000001", accountNumber);
//    }
}