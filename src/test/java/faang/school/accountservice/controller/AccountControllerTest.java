package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cache.OwnerCacheVersionService;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AccountControllerTest extends BaseContextTest {

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private OwnerCacheVersionService ownerCacheVersionService;

    private CreateAccountDto createAccountDto;

    @BeforeEach
    void setUp() {
        createAccountDto = new CreateAccountDto();
        createAccountDto.setOwnerType(OwnerType.USER);
        createAccountDto.setOwnerId(1L);
        createAccountDto.setAccountType(AccountType.INVESTMENT);
        createAccountDto.setCurrency("USD");
    }

    @BeforeEach
    void clearDb() {
        accountRepository.deleteAll();
    }

    @Test
    void testOpenAccount_Success() throws Exception {
        String response = mockMvc.perform(post("/accounts")
                        .header("x-user-id", createAccountDto.getOwnerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerType")
                        .value(createAccountDto.getOwnerType().toString()))
                .andExpect(jsonPath("$.ownerId").value(createAccountDto.getOwnerId()))
                .andExpect(jsonPath("$.accountType")
                        .value(createAccountDto.getAccountType().toString()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.toString()))
                .andReturn().getResponse().getContentAsString();

        AccountDto returnedDto = objectMapper.readValue(response, AccountDto.class);

        Optional<Account> savedAccountOptional = accountRepository.findById(returnedDto.getId());
        assertTrue(savedAccountOptional.isPresent(), "Account should be saved in the database");

        Account savedAccount = savedAccountOptional.get();
        assertEquals(createAccountDto.getOwnerId(), savedAccount.getOwnerId());
        assertEquals(createAccountDto.getAccountType(), savedAccount.getAccountType());
        assertEquals(AccountStatus.ACTIVE, savedAccount.getStatus());
        assertNotNull(savedAccount.getAccountNumber());

        verify(ownerCacheVersionService)
                .invalidateCache(createAccountDto.getOwnerId(), createAccountDto.getOwnerType());
    }

    @Test
    @Transactional
    public void testOpenAccount_Failure_OwnerIdNull() {
        CreateAccountDto invalidDto = new CreateAccountDto();
        invalidDto.setOwnerType(OwnerType.USER);
        invalidDto.setOwnerId(null);
        invalidDto.setAccountType(AccountType.INVESTMENT);
        invalidDto.setCurrency("USD");

        try {
            mockMvc.perform(post("/accounts")
                            .header("x-user-id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail("Expected BadRequest but got exception: " + e.getMessage());
        }
    }

    @Test
    void testOpenAccount_Failure_AccountAlreadyExists() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountNumber("1111111111111");
        newAccount.setOwnerType(createAccountDto.getOwnerType());
        newAccount.setOwnerId(createAccountDto.getOwnerId());
        newAccount.setAccountType(createAccountDto.getAccountType());
        newAccount.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(newAccount);

        mockMvc.perform(post("/accounts")
                        .header("x-user-id", createAccountDto.getOwnerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFreezeAccount_Success() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountNumber("1111111111111");
        newAccount.setOwnerType(createAccountDto.getOwnerType());
        newAccount.setOwnerId(createAccountDto.getOwnerId());
        newAccount.setAccountType(createAccountDto.getAccountType());
        newAccount.setStatus(AccountStatus.ACTIVE);
        Account savedAccount = accountRepository.save(newAccount);

        mockMvc.perform(patch("/accounts/{id}/freeze", savedAccount.getId())
                        .header("x-user-id", createAccountDto.getOwnerId()))
                .andExpect(status().isOk());

        Optional<Account> updatedAccountOptional = accountRepository.findById(savedAccount.getId());
        assertTrue(updatedAccountOptional.isPresent(),
                "Account should exist in the database");

        Account updatedAccount = updatedAccountOptional.get();
        assertEquals(AccountStatus.FROZEN, updatedAccount.getStatus(),
                "Account status should be updated to FROZEN");
        verify(ownerCacheVersionService)
                .invalidateCache(createAccountDto.getOwnerId(), createAccountDto.getOwnerType());
    }

    @Test
    void testUnfreezeAccount_Success() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountNumber("1111111111111");
        newAccount.setOwnerType(createAccountDto.getOwnerType());
        newAccount.setOwnerId(createAccountDto.getOwnerId());
        newAccount.setAccountType(createAccountDto.getAccountType());
        newAccount.setStatus(AccountStatus.FROZEN);
        Account savedAccount = accountRepository.save(newAccount);

        mockMvc.perform(patch("/accounts/{id}/unfreeze", savedAccount.getId())
                        .header("x-user-id", createAccountDto.getOwnerId()))
                .andExpect(status().isOk());

        Optional<Account> updatedAccountOptional = accountRepository.findById(savedAccount.getId());
        assertTrue(updatedAccountOptional.isPresent(),
                "Account should exist in the database");

        Account updatedAccount = updatedAccountOptional.get();
        assertEquals(AccountStatus.ACTIVE, updatedAccount.getStatus(),
                "Account status should be updated to ACTIVE");
        verify(ownerCacheVersionService)
                .invalidateCache(createAccountDto.getOwnerId(), createAccountDto.getOwnerType());
    }

    @Test
    void testGetAccountById_Success() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountNumber("1111111111111");
        newAccount.setOwnerType(createAccountDto.getOwnerType());
        newAccount.setOwnerId(createAccountDto.getOwnerId());
        newAccount.setAccountType(createAccountDto.getAccountType());
        newAccount.setStatus(AccountStatus.FROZEN);
        Account savedAccount = accountRepository.save(newAccount);

        mockMvc.perform(get("/accounts/{id}", savedAccount.getId())
                        .header("x-user-id", createAccountDto.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccount.getId()))
                .andExpect(jsonPath("$.ownerId").value(savedAccount.getOwnerId()))
                .andExpect(jsonPath("$.ownerType").value(savedAccount.getOwnerType().toString()))
                .andExpect(jsonPath("$.accountType").value(savedAccount.getAccountType().toString()))
                .andExpect(jsonPath("$.status").value(savedAccount.getStatus().toString()))
                .andExpect(jsonPath("$.accountNumber").value(savedAccount.getAccountNumber()));
    }

    @Test
    void testGetAccountsByOwner_Success_NoMocking() throws Exception {
        Long ownerId = 123L;
        OwnerType ownerType = OwnerType.USER;

        Account account1 = Account.builder()
                .accountNumber("1111111111111")
                .ownerId(ownerId)
                .ownerType(ownerType)
                .accountType(AccountType.INVESTMENT)
                .status(AccountStatus.ACTIVE)
                .build();
        Account account2 = Account.builder()
                .accountNumber("1111111111112")
                .ownerId(ownerId)
                .ownerType(ownerType)
                .accountType(AccountType.INVESTMENT)
                .status(AccountStatus.FROZEN)
                .build();
        Account account3 = Account.builder()
                .accountNumber("1111111111113")
                .ownerType(ownerType)
                .ownerId(ownerId)
                .accountType(AccountType.BUSINESS)
                .status(AccountStatus.ACTIVE)
                .build();
        accountRepository.saveAll(List.of(account1, account2, account3));

        OwnerRequest ownerRequest = new OwnerRequest(ownerId, ownerType);

        mockMvc.perform(get("/accounts/user?page=0&size=2&sort=id,asc")
                        .header("x-user-id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].ownerId").value(ownerId))
                .andExpect(jsonPath("$[0].ownerType").value(ownerType.toString()))
                .andExpect(jsonPath("$[0].accountType").value(AccountType.INVESTMENT.toString()))
                .andExpect(jsonPath("$[0].status").value(AccountStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$[1].ownerId").value(ownerId))
                .andExpect(jsonPath("$[1].ownerType").value(ownerType.toString()))
                .andExpect(jsonPath("$[1].accountType").value(AccountType.INVESTMENT.toString()))
                .andExpect(jsonPath("$[1].status").value(AccountStatus.FROZEN.toString()));
        verify(ownerCacheVersionService, times(2))
                .getCurrentVersion(ownerId, ownerType);
    }
}
