package faang.school.accountservice.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountCreateDto;
import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.enums.currency.Currency;
import faang.school.accountservice.mapper.account.SavingAccountMapperImpl;
import faang.school.accountservice.repository.account.SavingAccountRepository;
import faang.school.accountservice.service.tariff.TariffService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SavingAccountServiceTest {
    @Mock
    private SavingAccountRepository savingAccountRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private TariffService tariffService;
    @Spy
    private SavingAccountMapperImpl savingAccountMapper;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @InjectMocks
    private SavingAccountService savingAccountService;

    @Test
    void testFindEntityById() {
        final Long id = 1L;

        final SavingAccount mockSavingAccount = SavingAccount.builder()
                .id(id)
                .build();

        Mockito.when(savingAccountRepository.findById(id))
                .thenReturn(Optional.of(mockSavingAccount));

        SavingAccount savingAccount = savingAccountService.findEntityById(1L);

        assertAll(
                () -> assertEquals(id, savingAccount.getId())
        );
    }

    @Test
    void testFindEntityByIdNotFound() {
        Mockito.when(savingAccountRepository.findById(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> savingAccountService.findEntityById(1L));
    }

    @Test
    void testFindById() {
        final Long id = 1L;
        final SavingAccount mockSavingAccount = SavingAccount.builder()
                .id(id)
                .build();

        Mockito.when(savingAccountRepository.findById(id))
                .thenReturn(Optional.of(mockSavingAccount));

        SavingAccountDto savingAccountDto = savingAccountService.findById(id);

        assertAll(
                () -> assertEquals(id, savingAccountDto.getId())
        );
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(savingAccountRepository.findById(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> savingAccountService.findById(1L));
    }

    @Test
    void testOpenAccount() {
        final Tariff mockTariff = Tariff.builder()
                .id(2L)
                .name("PROMO")
                .rate(BigDecimal.valueOf(0.16))
                .build();

        final Account mockAccount = Account.builder()
                .id(3L)
                .paymentNumber("111")
                .type(AccountType.SAVING_ACCOUNT)
                .currency(Currency.EUR)
                .status(AccountStatus.ACTIVE)
                .build();

        final SavingAccount mockSavingAccount = SavingAccount.builder()
                .id(1L)
                .account(mockAccount)
                .tariff(mockTariff)
                .build();

        AccountDto accountDto = AccountDto.builder()
                .id(3L)
                .paymentNumber("111")
                .type(AccountType.SAVING_ACCOUNT)
                .currency(Currency.EUR)
                .status(AccountStatus.ACTIVE)
                .build();

        SavingAccountCreateDto createDto = SavingAccountCreateDto.builder()
                .tariffId(2L)
                .account(accountDto)
                .build();

        Mockito.when(tariffService.findEntityById(any()))
                .thenReturn(mockTariff);

        Mockito.when(accountService.getAccount(any()))
                .thenReturn(mockAccount);
        Mockito.when(accountService.openAccount(any()))
                .thenReturn(accountDto);

        Mockito.when(savingAccountRepository.save(any()))
                .thenReturn(mockSavingAccount);

        SavingAccountDto savingAccountDto = savingAccountService.openAccount(createDto);

        assertAll(
                () -> assertEquals(1L, savingAccountDto.getId()),
                () -> assertNotNull(savingAccountDto.getAccount()),
                () -> assertNotNull(savingAccountDto.getTariff())
        );
    }
}
