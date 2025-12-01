package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.ChangedBalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import faang.school.accountservice.exception.handler.GlobalExceptionHandler;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {
    private final static long DEFAULT_ID = 1L;
    private final static BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private final static LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.now();
    private final static String DEFAULT_ACCOUNT_NUMBER = "12345678901234567890";
    private final static BigDecimal ONE_HUNDRED_AMOUNT = BigDecimal.valueOf(100);
    private final static BigDecimal FIFTY_AMOUNT = BigDecimal.valueOf(50);

    private MockMvc mockMvc;

    private final long balanceId = DEFAULT_ID;
    private final long accountId = DEFAULT_ID;
    private final BigDecimal zeroBalance = DEFAULT_AMOUNT;
    private final BigDecimal oneHundredBalance = ONE_HUNDRED_AMOUNT;
    private final BigDecimal fiftyAmount = FIFTY_AMOUNT;
    private final LocalDateTime currentDateTime = DEFAULT_DATE_TIME;
    private final String accountNumber = DEFAULT_ACCOUNT_NUMBER;
    private final long userId = DEFAULT_ID;
    private final OwnerType ownerType = OwnerType.USER;
    private final AccountType accountType = AccountType.CURRENCY;
    private final Currency currency = Currency.USD;
    private final AccountStatus accountStatus = AccountStatus.ACTIVE;
    private final String createBalanceJson = String.format("""
                    {
                        "account_id": 1,
                        "actual_balance": 0,
                        "authorization_balance": 0,
                        "current_date_time": "%s"
                        }
                    """,
            currentDateTime);
    private final String updateBalanceJson = String.format("""
                    {
                        "account_id": 1,
                        "actual_balance": 100,
                        "authorization_balance": 0,
                        "current_date_time": "%s"
                        }
                    """,
            currentDateTime);
    private final String changeBalanceJson = """
                    {
                        "amount": 50
                    }
                    """;

    CreateBalanceDto createBalanceDto = CreateBalanceDto.builder()
            .accountId(accountId)
            .actualBalance(zeroBalance)
            .authorizationBalance(zeroBalance)
            .currentDateTime(currentDateTime)
            .build();

    UpdateBalanceDto updateBalanceDto = UpdateBalanceDto.builder()
            .accountId(accountId)
            .actualBalance(oneHundredBalance)
            .authorizationBalance(zeroBalance)
            .currentDateTime(currentDateTime)
            .build();

    ChangedBalanceDto changedBalanceDto = ChangedBalanceDto.builder()
            .amount(fiftyAmount)
            .build();

    Account account = Account.builder()
            .accountNumber(accountNumber)
            .ownerId(userId)
            .ownerType(ownerType)
            .accountType(accountType)
            .currency(currency)
            .status(accountStatus)
            .balance(null)
            .build();

    BalanceDto balanceDto = BalanceDto.builder()
            .id(balanceId)
            .account(account)
            .actualBalance(zeroBalance)
            .authorizationBalance(zeroBalance)
            .build();

    @InjectMocks
    private BalanceController balanceController;

    @Mock
    private BalanceService balanceService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(balanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testSuccessfullyBalanceCreated() throws Exception {
        when(balanceService.create(createBalanceDto)).thenReturn(balanceDto);
        mockMvc.perform(post("/v1/balances")
                        .contentType("application/json")
                        .content(createBalanceJson))
                .andExpect(status().isCreated());
        verify(balanceService, times(1)).create(createBalanceDto);
    }

    @Test
    public void testSuccessfullyBalanceUpdated() throws Exception {
        when(balanceService.update(balanceId, updateBalanceDto)).thenReturn(balanceDto);
        mockMvc.perform(put("/v1/balances/{balanceId}", balanceId)
                        .contentType("application/json")
                        .content(updateBalanceJson))
                .andExpect(status().isOk());
        verify(balanceService, times(1)).update(balanceId, updateBalanceDto);
    }

    @Test
    public void testSuccessfullyBalanceGet() throws Exception {
        when(balanceService.getBalanceById(balanceId)).thenReturn(balanceDto);
        mockMvc.perform(get("/v1/balances/{balanceId}", balanceId))
                .andExpect(status().isOk());
        verify(balanceService, times(1)).getBalanceById(balanceId);
    }

    @Test
    public void testSuccessfullyMakeAuthorization() throws Exception {
        when(balanceService.authorize(balanceId, changedBalanceDto)).thenReturn(balanceDto);
        mockMvc.perform(patch("/v1/balances/{balanceId}/authorize", balanceId)
                        .contentType("application/json")
                        .content(changeBalanceJson))
                .andExpect(status().isOk());
        verify(balanceService, times(1)).authorize(balanceId, changedBalanceDto);
    }

    @Test
    public void testSuccessfullyConfirmAuthorization() throws Exception {
        when(balanceService.confirm(balanceId, changedBalanceDto)).thenReturn(balanceDto);
        mockMvc.perform(patch("/v1/balances/{balanceId}/confirm", balanceId)
                        .contentType("application/json")
                        .content(changeBalanceJson))
                .andExpect(status().isOk());
        verify(balanceService, times(1)).confirm(balanceId, changedBalanceDto);
    }

    @Test
    public void testSuccessfullyReleaseAuthorization() throws Exception {
        when(balanceService.release(balanceId, changedBalanceDto)).thenReturn(balanceDto);
        mockMvc.perform(patch("/v1/balances/{balanceId}/release", balanceId)
                        .contentType("application/json")
                        .content(changeBalanceJson))
                .andExpect(status().isOk());
        verify(balanceService, times(1)).release(balanceId, changedBalanceDto);
    }

    @Test
    public void testFailBalanceCreateWhenAccountDoesNotExist() throws Exception {
        when(balanceService.create(createBalanceDto))
                .thenThrow(new AccountNotFoundException("Account not found"));
        mockMvc.perform(post("/v1/balances")
                        .contentType("application/json")
                        .content(createBalanceJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    public void testFailBalanceUpdateWhenBalanceRecordDoesNotExist() throws Exception {
        when(balanceService.update(balanceId, updateBalanceDto))
                .thenThrow(new BalanceNotFoundException("Balance not found"));
        mockMvc.perform(put("/v1/balances/{balanceId}", balanceId)
                        .contentType("application/json")
                        .content(updateBalanceJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Balance not found"));
    }

    @Test
    public void testFailBalanceUpdateWhenBalanceOperationInvalid() throws Exception {
        when(balanceService.authorize(balanceId, changedBalanceDto))
                .thenThrow(new InvalidBalanceOperationException("Amount must be positive"));
        mockMvc.perform(patch("/v1/balances/{balanceId}/authorize", balanceId)
                        .contentType("application/json")
                        .content(changeBalanceJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be positive"));
    }

    @Test
    public void testFailBalanceUpdateWhenBalanceInsufficient() throws Exception {
        when(balanceService.confirm(balanceId, changedBalanceDto))
                .thenThrow(new InsufficientBalanceException("Insufficient Balance"));
        mockMvc.perform(patch("/v1/balances/{balanceId}/confirm", balanceId)
                        .contentType("application/json")
                        .content(changeBalanceJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient Balance"));
    }
}
