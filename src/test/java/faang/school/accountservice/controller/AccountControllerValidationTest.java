package faang.school.accountservice.controller;

import faang.school.accountservice.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerValidationTest extends BaseIntegrationTest {

    @Test
    void testRejectInvalidAccountNumber() {
        webTestClient.get()
                .uri("/accounts/by-number/123")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testRejectInvalidOwnerId() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .queryParam("ownerId", -1)
                        .queryParam("ownerType", "USER")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testRejectInvalidOwnerType() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .queryParam("ownerId", 100)
                        .queryParam("ownerType", "INVALID_TYPE")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}
