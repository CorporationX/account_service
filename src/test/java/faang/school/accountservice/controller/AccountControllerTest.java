package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.service.AccountService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    private static final String VALID_NUMBER = "4200000000000001";
    private static final long RANDOM_ID = 1L;
    private static final String RANDOM_STRING = "test";
    private MockMvc mockMvc;
    private AccountDto dto;
    private ObjectWriter objectWriter;
    @Mock
    private AccountService service;
    @InjectMocks
    private AccountController controller;

    @BeforeEach
    void setUp() {
        //Arrange
        dto = AccountDto.builder()
                .id(RANDOM_ID)
                .number(VALID_NUMBER)
                .userId(RANDOM_ID)
                .projectId(null)
                .type(RANDOM_STRING)
                .currency(Currency.USD)
                .status(Status.ACTIVE)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetAccount() throws Exception {
        //Act
        Mockito.when(service.getAccount(VALID_NUMBER)).thenReturn(dto);
        //Assert
        mockMvc.perform(get("/api/account/4200000000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RANDOM_ID))
                .andExpect(jsonPath("$.number").value(VALID_NUMBER))
                .andExpect(jsonPath("$.userId").value(RANDOM_ID))
                .andExpect(jsonPath("$.type").value(RANDOM_STRING))
                .andExpect(jsonPath("$.currency").value(Currency.USD.toString()))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.toString()));
    }

    @Test
    void testOpenAccount() throws Exception {
        //Act
        Mockito.when(service.openAccount(dto)).thenReturn(dto);
        //Assert
        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RANDOM_ID))
                .andExpect(jsonPath("$.number").value(VALID_NUMBER))
                .andExpect(jsonPath("$.userId").value(RANDOM_ID))
                .andExpect(jsonPath("$.type").value(RANDOM_STRING))
                .andExpect(jsonPath("$.currency").value(Currency.USD.toString()))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.toString()));
    }

    @Test
    void updateAccountStatus() throws Exception {
        //Assert
        mockMvc.perform(put("/api/account/4200000000000001")
                        .param("status", "CLOSED"))
                .andExpect(status().isOk());
    }
}