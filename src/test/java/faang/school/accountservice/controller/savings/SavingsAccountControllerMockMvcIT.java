package faang.school.accountservice.controller.savings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.savings.SavingsAccountCreateDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SavingsAccountControllerMockMvcIT {

  private final static String URL_BASE = "/api/v1/savings";
  private final static String URL_SUFFIX_ADD = "/add";
  private final static String URL_SUFFIX_GET_BY_ID = "/{id}";
  private final static String URL_SUFFIX_GET_BY_OWNER_ID = "/{ownerId}";
  private final static String HEADER_KEY = "x-user-id";

  @Autowired
  private MockMvc mockMvc;

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Container
  public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
      "postgres:13:6");

  @DynamicPropertySource
  static void start(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    registry.add("spring.liquibase.contexts", () -> "test");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void addSavingsAccount() throws Exception {
    String url = URL_BASE + URL_SUFFIX_ADD;
    AccountDtoOpen accountDto = AccountDtoOpen.builder()
        .accountType(AccountType.SAVINGS)
        .currency(Currency.EUR)
        .ownerId(1L)
        .ownerType(OwnerType.USER)
        .notes("New Savings Account")
        .build();
    SavingsAccountCreateDto dto = SavingsAccountCreateDto.builder()
        .account(accountDto)
        .tariffId(1L)
        .build();

    mockMvc.perform(post(url)
            .header(HEADER_KEY, 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.account.notes").value("New Savings Account"));
  }

  @Test
  void getSavingsAccountById() throws Exception {
    String url = URL_BASE + URL_SUFFIX_GET_BY_ID;
    mockMvc.perform(MockMvcRequestBuilders.get(url, 1L)
            .header("x-user-id", 1L))
        .andExpect(status().isOk());
  }

  @Test
  void getSavingsAccountByOwnerId() throws Exception {
    String url = URL_BASE + URL_SUFFIX_GET_BY_OWNER_ID;
    mockMvc.perform(MockMvcRequestBuilders.get(url, 1L)
            .header("x-user-id", 1L))
        .andExpect(status().isOk());
  }
}