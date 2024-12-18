package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountOwnerRequest;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountOwnerControllerIT extends BaseContextTest {
    private static final Long OWNER_ID = 1L;

    @Test
    void createOwnerTest() throws Exception {
        String ownerRequest = objectMapper.writeValueAsString(new AccountOwnerRequest(OWNER_ID, OwnerType.USER));

        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", OWNER_ID)
                        .content(ownerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
                .andExpect(jsonPath("$.ownerType").value(OwnerType.USER.toString()));
    }

    @Test
    void getOwnerWithAccountsTest() throws Exception {
        mockMvc.perform(get("/api/v1/owners/search")
                        .header("x-user-id", OWNER_ID)
                        .param("ownerId", String.valueOf(OWNER_ID))
                        .param("ownerType", String.valueOf(OwnerType.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
                .andExpect(jsonPath("$.accounts").isArray());
    }
}
