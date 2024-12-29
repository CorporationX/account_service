package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountCreateDto;
import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.enums.currency.Currency;
import faang.school.accountservice.service.account.SavingAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SavingAccountControllerTest {
    private MockMvc mockMvc;

    @Mock
    private SavingAccountService savingAccountService;

    @InjectMocks
    private SavingAccountController savingAccountController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(savingAccountController).build();
    }

    @Test
    void testFindById() throws Exception {
        Mockito.when(savingAccountService.findById(1L))
                .thenReturn(provideSavingAccountDto(1L, 2L, 3L));

        mockMvc.perform(get("/accounts/saving/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.tariff.id").value("2"))
                .andExpect(jsonPath("$.tariff.name").value("test"))
                .andExpect(jsonPath("$.tariff.rate").value("0.16"))
                .andExpect(jsonPath("$.tariff.rateHistory").value("{}"))
                .andExpect(jsonPath("$.account.id").value("3"))
                .andExpect(jsonPath("$.account.paymentNumber").value("123"))
                .andExpect(jsonPath("$.account.currency").value("EUR"))
                .andExpect(jsonPath("$.account.type").value("SAVING_ACCOUNT"))
                .andExpect(jsonPath("$.account.status").value("ACTIVE"));
    }

    @Test
    void testFindBy() throws Exception {
        Mockito.when(savingAccountService.findBy(any()))
                .thenReturn(List.of(
                                provideSavingAccountDto(1L, 2L, 3L),
                                provideSavingAccountDto(4L, 5L, 6L)
                        )
                );

        mockMvc.perform(get("/accounts/saving?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].tariff.id").value("2"))
                .andExpect(jsonPath("$[0].tariff.name").value("test"))
                .andExpect(jsonPath("$[0].tariff.rate").value("0.16"))
                .andExpect(jsonPath("$[0].tariff.rateHistory").value("{}"))
                .andExpect(jsonPath("$[0].account.id").value("3"))
                .andExpect(jsonPath("$[0].account.paymentNumber").value("123"))
                .andExpect(jsonPath("$[0].account.currency").value("EUR"))
                .andExpect(jsonPath("$[0].account.type").value("SAVING_ACCOUNT"))
                .andExpect(jsonPath("$[0].account.status").value("ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value("4"))
                .andExpect(jsonPath("$[1].tariff.id").value("5"))
                .andExpect(jsonPath("$[1].tariff.name").value("test"))
                .andExpect(jsonPath("$[1].tariff.rate").value("0.16"))
                .andExpect(jsonPath("$[1].tariff.rateHistory").value("{}"))
                .andExpect(jsonPath("$[1].account.id").value("6"))
                .andExpect(jsonPath("$[1].account.paymentNumber").value("123"))
                .andExpect(jsonPath("$[1].account.currency").value("EUR"))
                .andExpect(jsonPath("$[1].account.type").value("SAVING_ACCOUNT"))
                .andExpect(jsonPath("$[1].account.status").value("ACTIVE"));
    }

    @Test
    void testCreate() throws Exception {
        Mockito.when(savingAccountService.openAccount(any()))
                .thenReturn(provideSavingAccountDto(1L, 2L, 3L));


        SavingAccountCreateDto createDto = SavingAccountCreateDto.builder()
                .tariffId(2L)
                .account(provideAccountDto(3L))
                .build();

        mockMvc.perform(post("/accounts/saving")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.tariff.id").value("2"))
                .andExpect(jsonPath("$.tariff.name").value("test"))
                .andExpect(jsonPath("$.tariff.rate").value("0.16"))
                .andExpect(jsonPath("$.tariff.rateHistory").value("{}"))
                .andExpect(jsonPath("$.account.id").value("3"))
                .andExpect(jsonPath("$.account.paymentNumber").value("123"))
                .andExpect(jsonPath("$.account.currency").value("EUR"))
                .andExpect(jsonPath("$.account.type").value("SAVING_ACCOUNT"))
                .andExpect(jsonPath("$.account.status").value("ACTIVE"));
    }

    @Test
    void testNotValidCreate() throws Exception {
        SavingAccountCreateDto createDto = SavingAccountCreateDto.builder()
                .account(provideAccountDto(3L))
                .build();

        mockMvc.perform(post("/accounts/saving")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isBadRequest());
    }


        private SavingAccountDto provideSavingAccountDto(Long id, Long tariffId, Long accountId) {
        return SavingAccountDto.builder()
                .id(id)
                .account(provideAccountDto(accountId))
                .tariff(TariffDto.builder()
                        .id(tariffId)
                        .name("test")
                        .rate(BigDecimal.valueOf(0.16))
                        .rateHistory("{}")
                        .build()
                )
                .build();
    }

    private AccountDto provideAccountDto(Long id) {
        return AccountDto.builder()
                .id(id)
                .paymentNumber("123")
                .currency(Currency.EUR)
                .type(AccountType.SAVING_ACCOUNT)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
