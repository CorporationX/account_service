package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.owner.OwnerMapper;
import faang.school.accountservice.mapper.type.TypeMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.owner.OwnerRepository;
import faang.school.accountservice.repository.type.TypeRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseContextTest {

    private static final String OWNER_NAME = "OWNER_NAME";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String USER_HEADER = "x-user-id";

    private static final long ID = 1L;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

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
    }

    @Nested
    class Block {

        @BeforeEach
        void init() {
            account = accountRepository.findById(ID).get();
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
        }

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
    }

    @Nested
    class Delete {

        @BeforeEach
        void init() {
            account = accountRepository.findById(ID).get();
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
        }

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
    }

    @Nested
    class Open {

        private AccountCreateDto accountCreateDto;
        private OwnerDto ownerDto;
        private TypeDto typeDto;

        @BeforeEach
        void init() {
            accountCreateDto = AccountCreateDto.builder()
                    .currency(Currency.RUB)
                    .build();

            ownerDto = OwnerDto.builder()
                    .name(OWNER_NAME)
                    .build();

            typeDto = TypeDto.builder()
                    .name(TYPE_NAME)
                    .build();
        }

        @Test
        @DisplayName("When /open request with correct body should return ok")
        void whenBodyIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            accountCreateDto.setType(typeDto);
            accountCreateDto.setOwner(ownerDto);

            typeRepository.save(typeMapper.toEntity(typeDto));
            ownerRepository.save(ownerMapper.toEntity(ownerDto));

            mockMvc.perform(
                            MockMvcRequestBuilders.post("/v1/accounts/open")
                                    .header(USER_HEADER, ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(accountCreateDto))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.owner.name").value(OWNER_NAME))
                    .andExpect(jsonPath("$.currency").value("RUB"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.type.name").value(TYPE_NAME));
        }

        @Test
        @DisplayName("When /open request with incorrect body should return bad request")
        void whenBodyIsIncorrectWhileRequestThenExpectBadRequestError() throws Exception {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/v1/accounts/open")
                                    .header(USER_HEADER, ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(accountCreateDto))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("When /open request with empty body should return internalServerError")
        void whenBodyIsMissingWhileRequestThenExpectInternalServerError() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/v1/accounts/open")
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept("{}")
            ).andExpect(status().isInternalServerError());
        }
    }
}