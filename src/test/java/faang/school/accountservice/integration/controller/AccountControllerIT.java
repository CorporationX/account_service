//package faang.school.accountservice.integration.controller;
//
//import faang.school.accountservice.integration.IntegrationTestBase;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//class AccountControllerIT extends IntegrationTestBase {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void getAllAccounts_shouldReturnAllAccountsForOwner() throws Exception {
//        mockMvc.perform(get("/api/v1/accounts/owners")
//                        .header("x-user-id", "1"))
//                .andExpectAll(status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$.length()").value(1),
//                        jsonPath("$[0].id").value(1),
//                        jsonPath("$[0].ownerId").value(1),
//                        jsonPath("$[0].ownerType").value("USER"),
//                        jsonPath("$[0].accountType").value("BUDGET_ACCOUNT"),
//                        jsonPath("$[0].currency").value("USD"),
//                        jsonPath("$[0].accountStatus").value("ACTIVE")
//                );
//    }
//
//    @Test
//    void getAccount_shouldReturnAccountById() throws Exception {
//        mockMvc.perform(get("/api/v1/accounts/{id}", 1)
//                        .header("x-user-id", "1"))
//                .andExpectAll(status().isOk(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$.id").value(1),
//                        jsonPath("$.ownerId").value(1),
//                        jsonPath("$.ownerType").value("USER"),
//                        jsonPath("$.accountType").value("BUDGET_ACCOUNT"),
//                        jsonPath("$.currency").value("USD"),
//                        jsonPath("$.accountStatus").value("ACTIVE")
//                );
//    }
//
//    @Test
//    void openAccount_shouldCreateAccount() throws Exception {
//        String requestBody = """
//                {
//                    "ownerType": "USER",
//                    "accountType": "BUDGET_ACCOUNT",
//                    "currency": "USD"
//                }
//                """;
//        mockMvc.perform(post("/api/v1/accounts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("x-user-id", "1")
//                        .content(requestBody)
//                )
//                .andExpectAll(status().isCreated(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        jsonPath("$.id").value(4),
//                        jsonPath("$.ownerId").value(1),
//                        jsonPath("$.ownerType").value("USER"),
//                        jsonPath("$.accountType").value("BUDGET_ACCOUNT"),
//                        jsonPath("$.currency").value("USD"),
//                        jsonPath("$.accountStatus").value("ACTIVE")
//                ).andDo(result -> System.out.println(result.getResponse().getContentAsString()));
//    }
//
//    @Test
//    void blockAccount_shouldBlockAccount() throws Exception {
//        mockMvc.perform(put("/api/v1/accounts/blocking/{id}", 1)
//                        .header("x-user-id", "1")
//                )
//                .andExpectAll(status().isNoContent()
//                );
//    }
//
//    @Test
//    void closeAccount_shouldBlockAccount() throws Exception {
//        mockMvc.perform(put("/api/v1/accounts/closing/{id}", 1)
//                        .header("x-user-id", "1")
//                )
//                .andExpectAll(status().isNoContent()
//                );
//    }
//
//}