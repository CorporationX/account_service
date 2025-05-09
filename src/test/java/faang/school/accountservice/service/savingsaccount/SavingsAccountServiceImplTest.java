package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceImplTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @InjectMocks
    private SavingsAccountServiceImpl savingsAccountService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void openSavingsAccount_ShouldSuccessfullyCreateAccount() {
        OpenSavingsAccountDto dto = new OpenSavingsAccountDto();
        Account account = new Account();

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistory(new ArrayList<>(List.of(1L)));
        savingsAccount.setLastInterestDate(LocalDateTime.now());

        SavingsAccountResponseDto expectedResponse = new SavingsAccountResponseDto();

        when(accountRepository.findById(anyString())).thenReturn(Optional.of(account));
        when(savingsAccountRepository.existsByAccountId(anyString())).thenReturn(false);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);
        when(savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount)).thenReturn(expectedResponse);

        SavingsAccountResponseDto result = savingsAccountService.openSavingsAccount(dto);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }
}