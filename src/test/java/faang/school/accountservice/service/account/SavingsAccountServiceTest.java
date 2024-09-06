package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.mapper.account.SavingsAccountMapper;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.validator.SavingsAccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class SavingsAccountServiceTest {

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @Mock
    private SavingsAccountValidator savingsAccountValidator;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TariffService tariffService;

    private SavingsAccount savingsAccount;

    private SavingsAccountDto savingsAccountDto;

    private UUID savingsAccountId;

    private UUID tariffId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        savingsAccountId = UUID.randomUUID();
        tariffId = UUID.randomUUID();
        savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setAccountId(savingsAccountId);
    }

    @Test
    public void testCreateSavingsAccount_Success() {
        Account account = new Account();
        account.setId(savingsAccountId);

        when(savingsAccountMapper.toEntity(savingsAccountDto)).thenReturn(savingsAccount);
        when(accountService.getAccountById(savingsAccountDto.getAccountId())).thenReturn(new AccountDto());
        when(accountMapper.toEntity(any(AccountDto.class))).thenReturn(account);
        when(savingsAccountRepository.save(savingsAccount)).thenReturn(savingsAccount);

        savingsAccountDto.setTariffHistory("[" + tariffId + "]");
        when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.create(savingsAccountDto, tariffId.toString());

        assertEquals(savingsAccountDto.getAccountId(), result.getAccountId());
        assertEquals(savingsAccountDto.getTariffHistory(), result.getTariffHistory());
    }

    @Test
    public void testCreateSavingsAccount_ValidationFails() {
        doThrow(new RuntimeException("The account with id " + savingsAccountId + " does not exist!"))
                .when(savingsAccountValidator).validateAccount(savingsAccountId);

        assertThrows(RuntimeException.class, () -> savingsAccountService.create(savingsAccountDto, tariffId.toString()));
    }

    @Test
    public void testUpdateSavingsAccountHistory_Success() {
        UUID newTariffId = UUID.randomUUID();

        savingsAccount.setTariffHistory("[" + tariffId + "]");
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));

        when(savingsAccountRepository.save(savingsAccount)).thenReturn(savingsAccount);

        savingsAccountDto.setTariffHistory("[" + tariffId + "," + newTariffId + "]");
        when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.updateSavingsAccountHistory(savingsAccountId, newTariffId.toString());

        assertEquals(savingsAccountDto.getTariffHistory(), result.getTariffHistory());
    }

    @Test
    public void testUpdateSavingsAccountHistory_ValidationFails() {
        UUID theSameTariffId = tariffId;

        doThrow(new RuntimeException("The rates cannot be equal!"))
                .when(savingsAccountValidator).validateTariffForUpdates(tariffId.toString(), theSameTariffId.toString());

        assertThrows(RuntimeException.class, () ->
                savingsAccountService.updateSavingsAccountHistory(savingsAccountId,
                        theSameTariffId.toString()));


    }
}
