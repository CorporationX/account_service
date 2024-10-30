package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapperImpl;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceImplTest {
    // TODO asdfg

    @Spy
    private SavingsAccountMapperImpl savingsAccountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @Mock
    private SavingsAccountRateRepository savingsAccountRateRepository;

    @Mock
    private FreeAccountNumbersServiceImpl freeAccountNumbersServiceImpl;

    @InjectMocks
    private SavingsAccountServiceImpl savingsAccountService;

    @Captor
    ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor;

    @Captor
    ArgumentCaptor<TariffHistory> tariffHistoryArgumentCaptor;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testOpenSavingsAccount() {
        Long tariffId = 1L;
        Long accountId = 2L;
        SavingsAccountDto dto = new SavingsAccountDto();
        dto.setTariffId(tariffId);
        dto.setAccountId(accountId);
        Tariff tariff = Tariff.builder()
                .id(tariffId)
                .name("tariff1").build();
        Account account = Account.builder()
                .id(dto.getAccountId()).build();
        when(tariffRepository.findById(dto.getTariffId())).thenReturn(Optional.of(tariff));
        when(accountRepository.findById(dto.getAccountId())).thenReturn(Optional.of(account));
        when(savingsAccountRepository.save(savingsAccountArgumentCaptor.capture()))
                .thenReturn(SavingsAccount.builder().account(account).build());

        SavingsAccountDto resultDto = savingsAccountService.openSavingsAccount(dto);

        verify(tariffRepository, times(1)).findById(dto.getTariffId());
        verify(accountRepository, times(1)).findById(dto.getAccountId());
        verify(savingsAccountRepository, times(1)).save(savingsAccountArgumentCaptor.capture());
        verify(tariffHistoryRepository, times(1)).save(tariffHistoryArgumentCaptor.capture());
        assertEquals(dto.getAccountId(), resultDto.getAccountId());
    }

}