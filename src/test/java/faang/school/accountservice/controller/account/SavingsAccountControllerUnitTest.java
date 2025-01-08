package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.service.account.SavingsAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavingsAccountController.class)
public class SavingsAccountControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserContext userContext;

    @MockBean
    private SavingsAccountServiceImpl savingsAccountService;

    private SavingsAccountDto savingsAccountDto;
    private Long savingsAccountId;

    @BeforeEach
    public void setUp() {
        savingsAccountId = 1L;
        savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setId(savingsAccountId);
        savingsAccountDto.setRate(BigDecimal.valueOf(1.8));
        savingsAccountDto.setTariffId(3L);
    }

    @Test
    @DisplayName("Should successfully open savings account")
    public void openSavingsAccount() throws Exception {
        savingsAccountDto.setId(null);
        String json = objectMapper.writeValueAsString(savingsAccountDto);
        when(savingsAccountService.openSavingsAccount(savingsAccountDto)).thenReturn(savingsAccountDto);

        savingsAccountService.openSavingsAccount(savingsAccountDto);

        mockMvc.perform(post("/api/v1/savings-account")
                        .header("x-user-id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Should return savings account by ID")
    public void getSavingsAccountById() throws Exception {
        when(savingsAccountService.getSavingsAccount(savingsAccountId)).thenReturn(savingsAccountDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account/{id}", savingsAccountDto.getId())
                        .header("x-user-id", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(savingsAccountDto.getId()))
                .andExpect(jsonPath("$.rate").value(savingsAccountDto.getRate()))
                .andExpect(jsonPath("$.tariffId").value(savingsAccountDto.getTariffId()));
    }

    @Test
    @DisplayName("Should return list of savings accounts by user ID")
    public void getSavingsAccountsByUserId() throws Exception {
        List<SavingsAccountDto> resultDtos = List.of(new SavingsAccountDto(), new SavingsAccountDto());
        when(savingsAccountService.getSavingsAccountByUserId(savingsAccountId)).thenReturn(resultDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account")
                        .param("userId", String.valueOf(1L))
                        .header("x-user-id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}