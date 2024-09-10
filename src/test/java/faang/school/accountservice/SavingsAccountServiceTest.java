package faang.school.accountservice;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.exception.BadRequestException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.SavingsAccountService;
import faang.school.accountservice.service.SavingsInterestPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private SavingsInterestPaymentService savingsInterestPaymentService;

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    private Account account;
    private SavingsAccount savingsAccount;
    private Tariff tariff;
    private TariffDto tariffDto;

    @BeforeEach
    void init() {
        account = new Account();
        account.setId(1L);
        savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setAccountId(1L);
        savingsAccount.setLastSuccessPercentDate(LocalDate.now());
        savingsAccount.setTariffHistory(Arrays.asList(1L));

        tariff = new Tariff();
        tariff.setId(1L);
        tariff.setType(TariffType.PROMO);

        tariffDto = new TariffDto();
        tariffDto.setType(TariffType.PROMO);
    }

    @Test
    @DisplayName("Test create savings account | Successfully")
    public void testCreateSavingsAccountOk() {
        when(tariffRepository.findTariffByType(any())).thenReturn(tariff);

        savingsAccountService.create(account, TariffType.PROMO);

        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }

    @Test
    @DisplayName("Test create savings account | Missing tariff type")
    public void testCreateSavingsAccountMissingTariff() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            savingsAccountService.create(account, null);
        });
        assertEquals("The selected tariff id is not specified", exception.getMessage());
    }

    @Test
    @DisplayName("Test update savings account tariff | Successfully")
    public void testUpdateSavingsAccountTariffOk() {
        when(savingsAccountRepository.findSavingsAccountByAccountId(1L)).thenReturn(savingsAccount);
        when(tariffRepository.findTariffByType(any())).thenReturn(tariff);

        savingsAccountService.updateSavingsAccountTariff(1L, TariffType.SUBSCRIBE);

        assertEquals(2L, savingsAccount.getTariffHistory().size());
    }

    @Test
    @DisplayName("Test update savings account tariff | Account not found")
    public void testUpdateSavingsAccountTariffAccountNotFound() {
        when(savingsAccountRepository.findSavingsAccountByAccountId(1L)).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            savingsAccountService.updateSavingsAccountTariff(1L, TariffType.PROMO);
        });
        assertEquals("Not found savings account with id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Test get savings account tariff by account ID | Account not found")
    public void testGetSavingsAccountTariffByAccountIdNotFound() {
        when(savingsAccountRepository.findSavingsAccountByAccountId(1L)).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            savingsAccountService.getSavingsAccountTariffByAccountId(1L);
        });
        assertEquals("Not found savings account with accountId: 1", exception.getMessage());
    }
}