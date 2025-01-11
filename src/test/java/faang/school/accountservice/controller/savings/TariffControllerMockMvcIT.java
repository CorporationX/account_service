package faang.school.accountservice.controller.savings;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.service.savings.TariffService;
import java.math.BigDecimal;
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
class TariffControllerMockMvcIT {

  private final static String URL_BASE = "/api/v1/savings/tariffs";
  private final static String URL_SUFFIX_ADD = "/add";
  private final static String URL_SUFFIX_GET_BY_ID = "/{id}";
  private final static String URL_SUFFIX_UPDATE = "/update";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TariffService tariffService;

  @Autowired
  private ObjectMapper objectMapper;

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
  void testInitPayAtAppStartScheduled() {
    // anyway AccountService App run scheduled task
  }

  @Test
  void testGetTariffById() throws Exception {
    String url = URL_BASE + URL_SUFFIX_GET_BY_ID;
    mockMvc.perform(MockMvcRequestBuilders.get(url, 1L)
            .header("x-user-id", 1L))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAllTariffs() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE)
            .header("x-user-id", 1L))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateTariff() throws Exception {
    String url = URL_BASE + URL_SUFFIX_UPDATE;
    TariffDto dto = TariffDto.builder()
        .id(1L)
        .title("Updated tariff title")
        .rate(BigDecimal.valueOf(1.9))
        .build();

    mockMvc.perform(MockMvcRequestBuilders.put(url)
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void addTariff() throws Exception {
    String url = URL_BASE + URL_SUFFIX_ADD;
    TariffDto dto = TariffDto.builder()
        .title("Added new tariff")
        .rate(BigDecimal.valueOf(1.9))
        .build();

    mockMvc.perform(MockMvcRequestBuilders.post(url)
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }
}