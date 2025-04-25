package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffHistory;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.SavingsAccountDuplicateException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {

    private final Long userId = 1L;
    private final Long firstTariffId = 2L;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Spy
    private SavingsAccountMapper savingsAccountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private Executor tariffRatesCalculator;

    @Mock
    private ProcessorSavingsAccountService processorSavingsAccountService;

    @BeforeEach
    void setUp() {
        savingsAccountService = new SavingsAccountService(savingsAccountRepository, savingsAccountMapper,
                accountRepository, userContext, tariffRepository, tariffRatesCalculator, processorSavingsAccountService);
    }

    @Test
    void testNegativeOpenSavingsAccountWhenParentAccountNotFound() {
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(AccountNotFoundException.class, () -> savingsAccountService.openSavingsAccount(firstTariffId));
    }

    @Test
    void testNegativeOpenSavingsAccountWhenTariffNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(createAccount(userId)));

        assertThrows(TariffNotFoundException.class, () -> savingsAccountService.openSavingsAccount(firstTariffId));
    }

    @Test
    void testNegativeOpenSavingsAccountWhenSavingsAccountExists() {
        when(userContext.getUserId()).thenReturn(userId);
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(createAccount(userId)));
        when(tariffRepository.findById(firstTariffId)).thenReturn(Optional.of(createTariff(firstTariffId)));
        when(savingsAccountRepository.save(any(SavingsAccount.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate savings account"));

        assertThrows(SavingsAccountDuplicateException.class, () -> savingsAccountService.openSavingsAccount(firstTariffId));
    }

    @Test
    void testPositiveOpenSavingsAccount() {
        Account account = createAccount(userId);
        Tariff tariff = createTariff(firstTariffId);
        SavingsAccountResponse response = createResponse(tariff);

        when(userContext.getUserId()).thenReturn(userId);
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(account));
        when(tariffRepository.findById(firstTariffId)).thenReturn(Optional.of(tariff));
        when(savingsAccountRepository.save(any(SavingsAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(response);

        SavingsAccountResponse result = savingsAccountService.openSavingsAccount(firstTariffId);

        ArgumentCaptor<SavingsAccount> captor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(savingsAccountRepository).save(captor.capture());

        SavingsAccount savedAccount = captor.getValue();
        assertNotNull(savedAccount.getAccountNumber());
        assertEquals(userId, savedAccount.getAccount().getOwnerId());
        assertEquals(1, savedAccount.getTariffHistory().size());
        assertEquals(tariff.getTypeName(), result.getActiveTariff());
    }

    @Test
    void testNegativeUpdateTariffOnAccountWhenAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () ->
                savingsAccountService.updateTariffOnSavingsAccount(userId, firstTariffId));
    }

    @Test
    void testNegativeUpdateTariffOnAccountWhenTariffNotFound() {
        Tariff tariff = createTariff(firstTariffId);
        Account account = createAccount(userId);
        when(savingsAccountRepository.findById(userId)).thenReturn(Optional.of(createSavingsAccount(tariff, account)));

        assertThrows(TariffNotFoundException.class, () ->
                savingsAccountService.updateTariffOnSavingsAccount(userId, firstTariffId));
    }

    @Test
    void testPositiveUpdateTariffOnAccount() {
        Tariff tariff = createTariff(firstTariffId);
        Account account = createAccount(userId);
        Long secondTariffId = 3L;
        Tariff newTariff = createTariff(secondTariffId);
        when(savingsAccountRepository.findById(userId)).thenReturn(Optional.of(createSavingsAccount(tariff, account)));
        when(tariffRepository.findById(firstTariffId)).thenReturn(Optional.of(newTariff));
        when(savingsAccountRepository.save(any(SavingsAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        savingsAccountService.updateTariffOnSavingsAccount(userId, firstTariffId);

        ArgumentCaptor<SavingsAccount> captor = ArgumentCaptor.forClass(SavingsAccount.class);
        verify(savingsAccountRepository).save(captor.capture());

        SavingsAccount savedAccount = captor.getValue();
        assertEquals(2, savedAccount.getTariffHistory().size());
    }

    @Test
    void testNegativeGetSavingsAccountByIdWhenAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> savingsAccountService.getSavingsAccountById(userId));
    }

    @Test
    void testNegativeGetSavingsAccountByIdWhenTariffNotFound() {
        Tariff tariff = createTariff(firstTariffId);
        Account account = createAccount(userId);
        SavingsAccountResponse response = createResponse(tariff);

        when(savingsAccountRepository.findById(userId)).thenReturn(Optional.of(createSavingsAccount(tariff, account)));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(response);

        assertThrows(TariffNotFoundException.class, () -> savingsAccountService.getSavingsAccountById(userId));
    }

    @Test
    void testPositiveGetSavingsAccountById() {
        Tariff tariff = createTariff(firstTariffId);
        Account account = createAccount(userId);
        SavingsAccountResponse response = createResponse(tariff);
        BigDecimal rate = BigDecimal.valueOf(0);

        when(savingsAccountRepository.findById(userId)).thenReturn(Optional.of(createSavingsAccount(tariff, account)));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(response);
        when(tariffRepository.findLatestRateByTariffTypeName(tariff.getTypeName())).thenReturn(Optional.of(rate));

        SavingsAccountResponse result = savingsAccountService.getSavingsAccountById(userId);

        assertEquals(rate.toString(), result.getActiveTariffRate());
        assertEquals(tariff.getTypeName(), result.getActiveTariff());
    }

    @Test
    void testNegativeGetAccountByOwnerIdWhenParentAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> savingsAccountService.getSavingsAccountByOwnerId(userId));
    }

    @Test
    void testPositiveGetSavingsAccountByOwnerId() {
        Tariff tariff = createTariff(firstTariffId);
        Account account = createAccount(userId);
        SavingsAccountResponse response = createResponse(tariff);
        BigDecimal rate = BigDecimal.valueOf(0);
        SavingsAccount savingsAccount = createSavingsAccount(tariff, account);

        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(account));
        when(savingsAccountRepository.findById(userId)).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toDto(any(SavingsAccount.class))).thenReturn(response);
        when(tariffRepository.findLatestRateByTariffTypeName(tariff.getTypeName())).thenReturn(Optional.of(rate));

        SavingsAccountResponse result = savingsAccountService.getSavingsAccountByOwnerId(userId);

        assertEquals(rate.toString(), result.getActiveTariffRate());
        assertEquals(tariff.getTypeName(), result.getActiveTariff());
    }

    private Account createAccount(Long ownerId) {
        return Account.builder()
                .id(ownerId)
                .ownerId(ownerId)
                .build();
    }

    private Tariff createTariff(Long tariffId) {
        return Tariff.builder()
                .id(tariffId)
                .typeName("test")
                .build();
    }

    private TariffHistory createTariffHistory(Tariff tariff, SavingsAccount account) {
        return TariffHistory.builder()
                .tariff(tariff)
                .savingsAccount(account)
                .build();
    }

    private SavingsAccount createSavingsAccount(Tariff tariff, Account account) {
        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .build();
        savingsAccount.setTariffHistory(List.of(createTariffHistory(tariff, savingsAccount)));
        return savingsAccount;
    }

    private SavingsAccountResponse createResponse(Tariff tariff) {
        return SavingsAccountResponse.builder()
                .activeTariff(tariff.getTypeName())
                .activeTariffRate(BigDecimal.valueOf(0).toString())
                .build();
    }
}
