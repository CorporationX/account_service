package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberServiceTest {
    private static final AccountType ACCOUNT_TYPE = AccountType.CHECKING;
    private static final BigInteger ACCOUNT_NUMBER = new BigInteger("42000000000000000001");

    @Mock
    private FreeAccountNumberRepository freeAccountNumberRepository;
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Mock
    private FreeAccountNumberMapper freeAccountNumberMapper;
    @Mock
    private ExecutorService executorService;
    private FreeAccountNumberService freeAccountNumberService;
    private FreeAccountNumber freeAccountNumber;
    private AccountNumbersSequence accountNumbersSequence;

    @BeforeEach
    void setUp() {
        freeAccountNumberService = new FreeAccountNumberService(freeAccountNumberRepository, accountNumbersSequenceRepository,
                freeAccountNumberMapper, executorService, 3, 3);
        freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumber.FreeAccountNumberKey(ACCOUNT_TYPE.getValue(), ACCOUNT_NUMBER))
                .accountType(ACCOUNT_TYPE.getValue())
                .accountNumber(ACCOUNT_NUMBER)
                .build();
        accountNumbersSequence = AccountNumbersSequence.builder()
                .id(1L)
                .accountType(ACCOUNT_TYPE.getValue())
                .currentNumber(new BigInteger("0"))
                .build();
    }

    @Test
    public void whenGetFreeNumberThenFreeNumber() {
        when(freeAccountNumberRepository.getFreeAccountNumbersCountByType(ACCOUNT_TYPE.getValue())).thenReturn(new BigInteger("5"));
        when(freeAccountNumberRepository.getFreeAccountNumber(ACCOUNT_TYPE.name())).thenReturn(Optional.of(freeAccountNumber));
        BigInteger actual = freeAccountNumberService.getFreeNumber(ACCOUNT_TYPE);
        assertThat(actual).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    public void whenGenerateFreeAccountThenFreeAccount() {
        when(accountNumbersSequenceRepository.findByAccountType(ACCOUNT_TYPE.getValue())).thenReturn(Optional.of(accountNumbersSequence));
        when(freeAccountNumberMapper.toFreeAccountNumber(any(), any())).thenReturn(freeAccountNumber);
        when(freeAccountNumberRepository.save(freeAccountNumber)).thenReturn(freeAccountNumber);
        FreeAccountNumber actual = freeAccountNumberService.generateFreeAccount(ACCOUNT_TYPE);
        verify(accountNumbersSequenceRepository).save(accountNumbersSequence);
        assertThat(actual).isEqualTo(freeAccountNumber);
    }
}