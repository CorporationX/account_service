package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.paymentAccount.CreatePaymentAccountDto;
import faang.school.accountservice.dto.paymentAccount.PaymentAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentAccountStatus;
import faang.school.accountservice.enums.PaymentAccountType;
import faang.school.accountservice.service.PaymentAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PaymentAccountV1ControllerTest {
    @InjectMocks
    private PaymentAccountV1Controller paymentAccountV1Controller;

    @Mock
    private PaymentAccountService paymentAccountService;

    private MockMvc mockMvc;
    private PaymentAccountDto paymentAccountDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentAccountV1Controller).build();
        paymentAccountDto = PaymentAccountDto.builder()
                .accountNumber("4444555566667777")
                .paymentAccountStatus(PaymentAccountStatus.ACTIVE)
                .accountType(PaymentAccountType.ACCUMULATIVE_ACCOUNT)
                .ownerId(1L)
                .currency(Currency.RUB)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testOpenPaymentAccount() throws Exception {
        CreatePaymentAccountDto createDto = CreatePaymentAccountDto.builder()
                .accountType(PaymentAccountType.ACCUMULATIVE_ACCOUNT)
                .currency(Currency.RUB)
                .ownerId(1L)
                .build();
        String createDtoJson = objectMapper.writeValueAsString(createDto);
        when(paymentAccountService.openPaymentAccount(createDto)).thenReturn(paymentAccountDto);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType("application/json")
                        .content(createDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value(paymentAccountDto.accountNumber()))
                .andExpect(jsonPath("$.paymentAccountStatus")
                        .value(paymentAccountDto.paymentAccountStatus().toString()))
                .andExpect(jsonPath("$.accountType").value(paymentAccountDto.accountType().toString()))
                .andExpect(jsonPath("$.currency").value(paymentAccountDto.currency().toString()))
                .andExpect(jsonPath("$.ownerId").value(paymentAccountDto.ownerId()));
        verify(paymentAccountService).openPaymentAccount(createDto);
    }

    @Test
    public void testOpenPaymentAccountBadRequest() throws Exception {
        CreatePaymentAccountDto createDto = CreatePaymentAccountDto.builder()
                .accountType(PaymentAccountType.ACCUMULATIVE_ACCOUNT)
                .ownerId(1L)
                .build();
        String createDtoJson = objectMapper.writeValueAsString(createDto);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType("application/json")
                        .content(createDtoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testClosePaymentAccount() throws Exception {
        when(paymentAccountService.closePaymentAccount(paymentAccountDto.accountNumber()))
                .thenReturn(paymentAccountDto);

        mockMvc.perform(put("/api/v1/accounts/%s/close".formatted(paymentAccountDto.accountNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(paymentAccountDto.accountNumber()))
                .andExpect(jsonPath("$.accountType").value(paymentAccountDto.accountType().toString()))
                .andExpect(jsonPath("$.currency").value(paymentAccountDto.currency().toString()))
                .andExpect(jsonPath("$.ownerId").value(paymentAccountDto.ownerId()));

        verify(paymentAccountService).closePaymentAccount(paymentAccountDto.accountNumber());
    }

    @Test
    public void testBlockPaymentAccount() throws Exception {
        when(paymentAccountService.blockPaymentAccount(paymentAccountDto.accountNumber()))
                .thenReturn(paymentAccountDto);

        mockMvc.perform(put("/api/v1/accounts/%s/block".formatted(paymentAccountDto.accountNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(paymentAccountDto.accountNumber()))
                .andExpect(jsonPath("$.accountType").value(paymentAccountDto.accountType().toString()))
                .andExpect(jsonPath("$.currency").value(paymentAccountDto.currency().toString()))
                .andExpect(jsonPath("$.ownerId").value(paymentAccountDto.ownerId()));

        verify(paymentAccountService).blockPaymentAccount(paymentAccountDto.accountNumber());
    }

    @Test
    public void testGetPaymentAccount() throws Exception {
        when(paymentAccountService.getPaymentAccountByNumber(paymentAccountDto.accountNumber()))
                .thenReturn(paymentAccountDto);

        mockMvc.perform(get("/api/v1/accounts/%s".formatted(paymentAccountDto.accountNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(paymentAccountDto.accountNumber()))
                .andExpect(jsonPath("$.accountType").value(paymentAccountDto.accountType().toString()))
                .andExpect(jsonPath("$.currency").value(paymentAccountDto.currency().toString()))
                .andExpect(jsonPath("$.ownerId").value(paymentAccountDto.ownerId()));

        verify(paymentAccountService).getPaymentAccountByNumber(paymentAccountDto.accountNumber());
    }

}
