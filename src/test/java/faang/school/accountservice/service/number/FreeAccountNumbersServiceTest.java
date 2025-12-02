package faang.school.accountservice.service.number;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersServiceTest {

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountSequenceService accountSequenceService;

    @Mock
    private AccountNumberProperties accountNumberProperties;

    @InjectMocks
    private FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Test
    @DisplayName("Should generate account numbers batch and save to repository")
    void shouldGenerateAccountNumbersAndSaveToRepository() {
        AccountType type = AccountType.CURRENT;
        int batchSize = 5;
        long prefix = 4200_0000_0000_0000L;

        Map<AccountType, Long> prefixMap = new EnumMap<>(AccountType.class);
        prefixMap.put(type, prefix);
        when(accountNumberProperties.getPrefix()).thenReturn(prefixMap);

        when(accountSequenceService.incrementCounter(type, batchSize))
                .thenReturn(new AccountPeriod(1L, batchSize));

        freeAccountNumbersService.generateAccountNumbers(type, batchSize);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FreeAccountNumber>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(freeAccountNumbersRepository).saveAll(captor.capture());

        List<FreeAccountNumber> saved = captor.getValue();
        assertThat(saved).hasSize(batchSize);

        List<String> expected = new ArrayList<>();
        for (long seq = 1; seq <= batchSize; seq++) {
            expected.add(Long.toString(prefix + seq));
        }

        List<String> actual = saved.stream()
                .map(it -> it.getId().getAccountNumber())
                .toList();

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("Should throw exception when batch size is zero")
    void shouldFailOnZeroBatchSize() {
        assertThrows(IllegalArgumentException.class,
                () -> freeAccountNumbersService.generateAccountNumbers(AccountType.SAVING, 0));

        verifyNoInteractions(accountSequenceService, freeAccountNumbersRepository);
    }

    @Test
    @DisplayName("Should use correct configured prefix for generated numbers")
    void shouldGenerateAccountNumbersWithCorrectPrefixFromConfig() {
        AccountType type = AccountType.CREDIT;
        int batchSize = 3;
        long prefix = 6011_0000_0000_0000L;
        long from = 11L;
        long to = from + batchSize - 1;

        Map<AccountType, Long> prefixMap = new EnumMap<>(AccountType.class);
        prefixMap.put(type, prefix);
        when(accountNumberProperties.getPrefix()).thenReturn(prefixMap);

        when(accountSequenceService.incrementCounter(type, batchSize))
                .thenReturn(new AccountPeriod(from, to));

        freeAccountNumbersService.generateAccountNumbers(type, batchSize);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FreeAccountNumber>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(freeAccountNumbersRepository).saveAll(captor.capture());

        List<FreeAccountNumber> saved = captor.getValue();
        assertThat(saved).hasSize(batchSize);

        List<String> expected = new ArrayList<>();
        for (long seq = from; seq <= to; seq++) {
            expected.add(Long.toString(prefix + seq));
        }

        List<String> actual = saved.stream()
                .map(it -> it.getId().getAccountNumber())
                .toList();

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("Should use existing free account number when available")
    void shouldUseExistingFreeAccountNumber() {
        AccountType type = AccountType.CURRENT;
        String freeNumber = "4200000000000001";
        FreeAccountNumber free = new FreeAccountNumber(new FreeAccountId(type, freeNumber));

        when(freeAccountNumbersRepository.findFirstForUpdate(type.name()))
                .thenReturn(free);

        AtomicReference<String> captured = new AtomicReference<>();

        freeAccountNumbersService.retrieveAccountNumber(type, captured::set);

        assertThat(captured.get()).isEqualTo(freeNumber);

        verify(freeAccountNumbersRepository).findFirstForUpdate(type.name());
        verify(freeAccountNumbersRepository)
                .deleteByTypeAndAccountNumber(type.name(), freeNumber);
        verifyNoInteractions(accountSequenceService);
    }

    @Test
    @DisplayName("Should generate new account number when no free numbers available")
    void shouldGenerateNewAccountNumberWhenNoFreeAvailable() {
        AccountType type = AccountType.SAVING;
        long prefix = 5236_0000_0000_0000L;
        long seq = 101L;
        String expectedNumber = Long.toString(prefix + seq);

        Map<AccountType, Long> prefixMap = new EnumMap<>(AccountType.class);
        prefixMap.put(type, prefix);
        when(accountNumberProperties.getPrefix()).thenReturn(prefixMap);

        when(freeAccountNumbersRepository.findFirstForUpdate(type.name()))
                .thenReturn(null);

        when(accountSequenceService.incrementCounter(type, 1))
                .thenReturn(new AccountPeriod(seq, seq));

        AtomicReference<String> captured = new AtomicReference<>();

        freeAccountNumbersService.retrieveAccountNumber(type, captured::set);

        assertThat(captured.get()).isEqualTo(expectedNumber);

        verify(freeAccountNumbersRepository).findFirstForUpdate(type.name());
        verify(freeAccountNumbersRepository, never())
                .deleteByTypeAndAccountNumber(anyString(), anyString());
        verify(accountSequenceService).incrementCounter(type, 1);
    }
}
