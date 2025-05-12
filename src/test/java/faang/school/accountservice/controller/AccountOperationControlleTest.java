package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountOperationViewDto;
import faang.school.accountservice.dto.OperationStatus;
import faang.school.accountservice.dto.OperationType;
import faang.school.accountservice.service.account.AccountOperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountOperationController.class)
class AccountOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountOperationService accountOperationService;

    @MockBean
    private UserContext userContext;

    private UUID operationId;
    private AccountOperationViewDto response;

    @BeforeEach
    void setUp() {
        operationId = UUID.randomUUID();
        operationId = UUID.randomUUID();
        response = new AccountOperationViewDto(
                operationId,
                OperationStatus.COMPLETED,
                OperationType.AUTHORIZATION,
                "Operation completed successfully"
        );
    }

    @Test
    void testGetOperation_Success() throws Exception {
        when(accountOperationService.getOperation(operationId)).thenReturn(response);

        mockMvc.perform(get("/api")
                        .header("x-user-id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operationId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(operationId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.operationType").value("AUTHORIZATION"))
                .andExpect(jsonPath("$.message").value("Operation completed successfully"));
    }
}
