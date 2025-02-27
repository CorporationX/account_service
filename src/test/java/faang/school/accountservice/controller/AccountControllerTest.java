package faang.school.accountservice.controller;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest extends BaseIntegrationTest {

    private AccountResponseDto createdAccount;

    @BeforeEach
    void setUp() {
        AccountCreateDto createDto = AccountCreateDto.builder()
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.USD)
                .build();

        createdAccount = webTestClient.post()
                .uri("/accounts/open")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void testGetAccountById() {
        webTestClient.get()
                .uri("/accounts/{id}", createdAccount.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdAccount.id())
                .jsonPath("$.accountNumber").isNotEmpty()
                .jsonPath("$.ownerId").isEqualTo(100)
                .jsonPath("$.ownerType").isEqualTo("USER");
    }

    @Test
    void testBlockAccount() {
        webTestClient.patch()
                .uri("/accounts/{id}/block", createdAccount.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountStatus").isEqualTo("BLOCKED");
    }

    @Test
    void testCloseAccount() {
        webTestClient.patch()
                .uri("/accounts/{id}/close", createdAccount.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountStatus").isEqualTo("CLOSED")
                .jsonPath("$.closedAt").isNotEmpty();
    }


    @Test
    void testReturnNotFoundForNonExistentAccount() {
        webTestClient.get()
                .uri("/accounts/999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetAccountByNumber() {
        webTestClient.get()
                .uri("/accounts/by-number/{accountNumber}", createdAccount.accountNumber())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdAccount.id())
                .jsonPath("$.accountNumber").isEqualTo(createdAccount.accountNumber());
    }

    @Test
    void testGetAccountsByOwner() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .queryParam("ownerId", createdAccount.ownerId())
                        .queryParam("ownerType", createdAccount.ownerType())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").value(val -> {
                    assertThat(val).isInstanceOf(Integer.class);
                    assertThat((Integer) val).isPositive();})
                .jsonPath("$[0].ownerId").isEqualTo(createdAccount.ownerId())
                .jsonPath("$[0].ownerType").isEqualTo(createdAccount.ownerType().toString());
    }
}
