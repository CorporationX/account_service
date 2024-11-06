package faang.school.accountservice.service.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.SavingsAccountCreatedDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.account.SavingsAccountMapper;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.service.tariff.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private TariffService tariffService;

    @Mock
    private ObjectMapper objectMapper;

    private static final Long ID = 1L;
    private static final Double INTEREST_RATE = 5.0;
    private static final BigDecimal BALANCE = BigDecimal.valueOf(100);
    private static final String TARIFF_TYPE = "tariff";
    private static final String TARIFF_HISTORY = "history";
    private Tariff tariff;
    private Account account;
    private SavingsAccount savingsAccount;
    private SavingsAccountDto savingsAccountDto;
    private SavingsAccountCreatedDto savingsAccountCreatedDto;

    @BeforeEach
    public void init() {
        tariff = Tariff.builder()
                .tariffName(TARIFF_TYPE)
                .rate(Rate.builder()
                        .interestRate(INTEREST_RATE)
                        .build())
                .build();
        account = Account.builder()
                .id(ID)
                .build();
        savingsAccount = SavingsAccount.builder()
                .id(ID)
                .balance(BALANCE)
                .account(account)
                .owner(Owner.builder()
                        .id(ID)
                        .build())
                .tariff(tariff)
                .tariffHistory(TARIFF_HISTORY)
                .build();

        savingsAccountDto = SavingsAccountDto.builder().build();
        savingsAccountCreatedDto = SavingsAccountCreatedDto.builder()
                .accountId(ID)
                .tariffName(TARIFF_TYPE)
                .build();
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех при создании SavingsAccount")
        public void whenOpenSavingsAccountThenCreateSavingsAccount() throws JsonProcessingException {
            when(accountService.getAccount(ID)).thenReturn(account);
            when(tariffService.getTariffByTariffType(TARIFF_TYPE)).thenReturn(tariff);
            when(objectMapper.writeValueAsString(anyList())).thenReturn(TARIFF_HISTORY);
            when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);
            when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

            SavingsAccountDto result = savingsAccountService.openSavingsAccount(savingsAccountCreatedDto);

            assertNotNull(result);
            verify(accountService).getAccount(ID);
            verify(tariffService).getTariffByTariffType(TARIFF_TYPE);
            verify(objectMapper).writeValueAsString(anyList());
            verify(savingsAccountRepository).save(any(SavingsAccount.class));
            verify(savingsAccountMapper).toDto(savingsAccount);
        }

        @Test
        @DisplayName("Успех при получении SavingsAccount по айди аккаунта")
        public void whenGetSavingsAccountByIdThenReturnEntity() {
            when(savingsAccountRepository.findById(ID)).thenReturn(Optional.of(savingsAccount));
            when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

            SavingsAccountDto result = savingsAccountService.getSavingsAccountById(ID);

            assertNotNull(result);
            verify(savingsAccountRepository).findById(ID);
            verify(savingsAccountMapper).toDto(savingsAccount);
        }

        @Test
        @DisplayName("Успех при получении SavingsAccount по айди владельца")
        public void whenGetSavingsAccountByOwnerIdThenReturnEntity() {
            when(savingsAccountRepository.findByOwnerId(ID)).thenReturn(Optional.of(savingsAccount));
            when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

            SavingsAccountDto result = savingsAccountService.getSavingsAccountByOwnerId(ID);

            assertNotNull(result);
            verify(savingsAccountRepository).findByOwnerId(ID);
            verify(savingsAccountMapper).toDto(savingsAccount);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка при получении SavingsAccount по айди аккаунта")
        public void whenGetSavingsAccountByIdThenReturnEntity() {
            when(savingsAccountRepository.findById(ID)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> savingsAccountService.getSavingsAccountById(ID));
        }

        @Test
        @DisplayName("Ошибка при получении SavingsAccount по айди владельца")
        public void whenGetSavingsAccountByOwnerIdThenReturnEntity() {
            when(savingsAccountRepository.findByOwnerId(ID)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> savingsAccountService.getSavingsAccountByOwnerId(ID));
        }
    }
}