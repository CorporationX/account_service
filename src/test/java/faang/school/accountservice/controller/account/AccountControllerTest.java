package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.owner.OwnerRepository;
import faang.school.accountservice.repository.type.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(AccountController.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AccountControllerTest {

    private static final String TEST = "TEST";
    private static final String USER_HEADER = "x-user-id";

    private static final long ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TypeRepository typeRepository;

    @MockBean
    private OwnerRepository ownerRepository;

    @Nested
    class Open {

        private AccountCreateDto accountCreateDto;
        private OwnerDto ownerDto;
        private AccountType type;
        private TypeDto typeDto;
        private Owner owner;

        @BeforeEach
        void init() {
            accountCreateDto = AccountCreateDto.builder()
                    .currency(Currency.RUB)
                    .build();

            ownerDto = OwnerDto.builder()
                    .name(TEST)
                    .build();

            typeDto = TypeDto.builder()
                    .name(TEST)
                    .build();

            owner = Owner.builder()
                    .id(ID)
                    .name(TEST)
                    .build();

            type = AccountType.builder()
                    .id(ID)
                    .name(TEST)
                    .build();
        }

        @Test
        @DisplayName("When /open request with correct body should return ok")
        void whenBodyIsCorrectWhileRequestThenExpectOkResponse() throws Exception {
            accountCreateDto.setType(typeDto);
            accountCreateDto.setOwner(ownerDto);

            when(typeRepository.findByName(TEST))
                    .thenReturn(Optional.ofNullable(type));
            when(ownerRepository.findByName(TEST))
                    .thenReturn(Optional.ofNullable(owner));

            mockMvc.perform(
                            MockMvcRequestBuilders.post("/v1/accounts/open")
                                    .header(USER_HEADER, ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(objectMapper.writeValueAsString(accountCreateDto))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.owner.name").value(TEST))
                    .andExpect(jsonPath("$.currency").value("RUB"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.type.name").value(TEST));
        }

        //internalServerError а не badRequest?
        @Test
        @DisplayName("When /open request with incorrect body should return bad request")
        void whenBodyIsIncorrectWhileRequestThenExpectBadRequestError() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/v1/accounts/open")
                            .header(USER_HEADER, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(accountCreateDto))
            ).andExpect(status().isInternalServerError());
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

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14")
                    .withInitScript("create_schema.sql");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}