package faang.school.accountservice.integration.audit.controller;

import faang.school.accountservice.integration.audit.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Sql("classpath:sql/dataBalanceAudit.sql")
public class BalanceAuditControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Запуск контейнера")
    void testContainerIsRunning() {
        assertThat(container.isRunning()).as("PostgreSQL контейнер не запущен!").isTrue();
    }

    @Test
    @DisplayName("Получение ответа JSON формате по ID")
    void getBalanceAuditTest() throws Exception {
        Long balanceAuditId = 1L;

        mockMvc.perform(get("/api/v1/balance-audit/balanceAuditId/{balanceAuditId}", balanceAuditId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        """
                                {
                                "balanceId": 1,
                                "version": 1,
                                "actualBalance": 23.00,
                                "authorizationBalance": 23.00,
                                "type": "TRANSLATION",
                                "auditAt" : "2024-07-01T14:00:00"
                                }
                                """
                ));
    }

    @Test
    @DisplayName("Получение списка JSON формате")
    void getAllBalanceAuditsTest() throws Exception {
        mockMvc.perform(get("/api/v1/balance-audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        """
                                [
                                 {
                                  "balanceId": 1,
                                  "version": 1,
                                  "actualBalance": 23.00,
                                  "authorizationBalance": 23.00,
                                  "type": "TRANSLATION",
                                  "auditAt" : "2024-07-01T14:00:00"
                                  },
                                  {
                                  "balanceId": 2,
                                  "version": 2,
                                  "actualBalance": 32.00,
                                  "authorizationBalance": 32.00,
                                  "type": "BOOKING",
                                  "auditAt" : "2024-07-01T16:00:00"
                                 }
                                ]
                                """
                ));
    }
}
