package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(UserContext.class)
class AccountControllerMvcTest {

    private static final String USER_HEADER = "x-user-id";

    private static final long ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Nested
    class Get {

        @Test
        @DisplayName("When get /id request with correct path variable should return ok")
        void whenVariableIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.get("/v1/accounts/{id}", ID)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isOk());
        }

        @Test
        @DisplayName("When get /id request with empty path variable should return not found")
        void whenBodyIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.get("/v1/accounts/{id}", (Object) null)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isNotFound());
        }
    }

    @Nested
    class Block {

        @Test
        @DisplayName("When patch /id/block request with correct path variable should return accepted")
        void whenVariableIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.patch("/v1/accounts/{id}/block", ID)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("When patch /id request with empty path variable should return not found")
        void whenBodyIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.patch("/v1/accounts/{id}/block", (Object) null)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isNotFound());
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("When delete /id request with correct path variable should return ok")
        void whenVariableIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.delete("/v1/accounts/{id}", ID)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("When delete /id request with empty path variable should return not found")
        void whenBodyIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.delete("/v1/accounts/{id}", (Object) null)
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
            ).andExpect(status().isNotFound());
        }
    }
}