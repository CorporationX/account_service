package faang.school.accountservice.controller;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest extends BaseIntegrationTest {

    private AccountResponseDto createdAccount;
    private AccountCreateDto createDto;

    @BeforeEach
    void setUp() {
        createDto = AccountCreateDto.builder()
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

    @Test
    @Disabled("Flaky test")
    void testOptimisticLocking() throws ExecutionException, InterruptedException {
        AccountResponseDto account = webTestClient.post()
                .uri("/accounts/open")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(account).isNotNull();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        Callable<HttpStatus> updateToBlocked = () -> sendPatchRequest("/accounts/{id}/block", account.id(), barrier);
        Callable<HttpStatus> updateToClosed = () -> sendPatchRequest("/accounts/{id}/close", account.id(), barrier);

        List<Future<HttpStatus>> results = executor.invokeAll(List.of(updateToBlocked, updateToClosed));

        HttpStatus status1 = results.get(0).get();
        HttpStatus status2 = results.get(1).get();

        assertTrue(status1 == HttpStatus.CONFLICT || status2 == HttpStatus.CONFLICT);

        executor.shutdown();
    }

    private HttpStatus sendPatchRequest(String uri, Long id, CyclicBarrier barrier) {
        try {
            barrier.await();

            webTestClient.get()
                    .uri("/accounts/{id}", id)
                    .exchange()
                    .expectStatus().isOk();

            return HttpStatus.valueOf(webTestClient.patch()
                    .uri(uri, id)
                    .exchange()
                    .returnResult(Void.class)
                    .getStatus()
                    .value());
        } catch (WebClientResponseException e) {
            return HttpStatus.valueOf(e.getStatusCode().value());
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}