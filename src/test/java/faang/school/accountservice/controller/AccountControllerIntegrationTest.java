package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.model.enums.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AccountControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

//    @MockBean
//    UserContext userContext;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testdb")
                    .withUsername("admin")
                    .withPassword("admin")
                    .withInitScript("schema_for_AccountController.sql");

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    void testGetAccountById() throws Exception {
        Long id = 5L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.number").value("59728975298"))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.toString()))
                .andReturn();
    }

    @Test
    void testGetAccountByNumber() throws Exception {
        String number = "2385627836527863";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/number/{number}", number))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.projectId").value(2))
                .andExpect(jsonPath("$.number").value(number))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.toString()))
                .andExpect(jsonPath("$.type").value(AccountType.BUSINESS.toString()))
                .andReturn();
    }

    @Test
    void testOpenAccountWithUserId() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUserId(5L);
        accountDto.setStatus(AccountStatus.ACTIVE);
        accountDto.setCurrency(Currency.EUR);
        accountDto.setType(AccountType.BUSINESS);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(13))
                .andExpect(jsonPath("$.userId").value(5L))
                .andExpect(jsonPath("$.status").value(accountDto.getStatus().toString()))
                .andExpect(jsonPath("$.currency").value(accountDto.getCurrency().toString()))
                .andExpect(jsonPath("$.type").value(accountDto.getType().toString()))
                .andReturn();
    }

    @Test
    void testBlockAccountById() throws Exception {
        Long id = 7L;

        mockMvc.perform(put("/api/v1/account/block/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.status").value(AccountStatus.BLOCKED.toString()))
                .andExpect(jsonPath("$.number").value("23892656235"))
                .andReturn();
    }

    @Test
    void testBlockAccountByNumber() throws Exception {
        String number = "59728975298";

        mockMvc.perform(put("/api/v1/account/block/number/{number}", number))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.status").value(AccountStatus.BLOCKED.toString()))
                .andExpect(jsonPath("$.number").value(number))
                .andReturn();
    }

//    @Test
//    void testGetAnalytics_withValidParams() throws Exception {
//        String startDate = "2024-10-09T16:37:34.252454600";
//        String endDate = "2024-10-11T16:37:34.252454600";
//        Long id = 3L;
//        String eventType = "PROFILE_APPEARED_IN_SEARCH";
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/analytics")
//                        .header("x-user-id", 8L)
//                        .param("id", id.toString())
//                        .param("eventType", eventType)
//                        .param("startDate", startDate)
//                        .param("endDate", endDate))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].eventType").value(eventType))
//                .andExpect(jsonPath("$[0].receiverId").value(id))
//                .andExpect(jsonPath("$[1].eventType").value(eventType))
//                .andExpect(jsonPath("$[1].receiverId").value(id))
//                .andReturn();
//
//        String jsonResponse = mvcResult.getResponse().getContentAsString();
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootArray = mapper.readTree(jsonResponse);
//        Iterator<JsonNode> elements = rootArray.elements();
//        List<LocalDateTime> localDateTimes = new ArrayList<>();
//        while (elements.hasNext()) {
//            JsonNode element = elements.next();
//            JsonNode receivedAtNode = element.get("receivedAt");
//            LocalDateTime receivedAt = LocalDateTime.of(
//                    receivedAtNode.get(0).asInt(),
//                    receivedAtNode.get(1).asInt(),
//                    receivedAtNode.get(2).asInt(),
//                    receivedAtNode.get(3).asInt(),
//                    receivedAtNode.get(4).asInt(),
//                    receivedAtNode.get(5).asInt(),
//                    receivedAtNode.get(6).asInt()
//            );
//            localDateTimes.add(receivedAt);
//        }
//
//        assertAll(
//                () -> assertTrue(localDateTimes.get(0).isAfter(LocalDateTime.parse(startDate))),
//                () -> assertTrue(localDateTimes.get(0).isBefore(LocalDateTime.parse(endDate))),
//                () -> assertTrue(localDateTimes.get(1).isAfter(LocalDateTime.parse(startDate))),
//                () -> assertTrue(localDateTimes.get(1).isBefore(LocalDateTime.parse(endDate)))
//        );
//    }
}
