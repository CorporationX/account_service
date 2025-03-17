package faang.school.savingsAccount;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.repository.SavingsAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AccountServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SavingsAccountControllerTest extends BaseIntegrationTest {

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    private static final String SAVING_ACCOUNT_URL = "api/v1/savingsAccount";
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
    void openSavingsAccount() throws Exception {
        TariffResponseDto createdTariff = webTestClient.post()
            .uri("/api/v1/tariff")
            .bodyValue(new TariffRequestDto(TariffType.ENTERPRISE, 15.0))
            .exchange()
            .expectStatus().isOk()
            .expectBody(TariffResponseDto.class)
            .returnResult()
            .getResponseBody();

        SavingsAccountResponseDto createdSavingAccount = webTestClient.post()
            .uri(SAVING_ACCOUNT_URL)
            .bodyValue(new SavingsAccountRequestDto(createdAccount.id(), TariffType.ENTERPRISE))
            .exchange()
            .expectStatus().isOk()
            .expectBody(SavingsAccountResponseDto.class)
            .returnResult()
            .getResponseBody();

        assertThat(createdSavingAccount).isNotNull();
        assertThat(createdSavingAccount.tariffName().toString()).isEqualTo("ENTERPRISE");
    }

    @Test
    void getSavingsAccountByAccountId() throws Exception {
        TariffResponseDto createdTariff = webTestClient.post()
            .uri("/api/v1/tariff")
            .bodyValue(new TariffRequestDto(TariffType.BASE, 15.0))
            .exchange()
            .expectStatus().isOk()
            .expectBody(TariffResponseDto.class)
            .returnResult()
            .getResponseBody();

        SavingsAccountResponseDto createdSavingAccount = webTestClient.post()
            .uri(SAVING_ACCOUNT_URL)
            .bodyValue(new SavingsAccountRequestDto(createdAccount.id(), TariffType.BASE))
            .exchange()
            .expectStatus().isOk()
            .expectBody(SavingsAccountResponseDto.class)
            .returnResult()
            .getResponseBody();

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(SAVING_ACCOUNT_URL + "/accountId")
                .queryParam("accountId", createdSavingAccount.accountId())
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.tariffName").isEqualTo(createdSavingAccount.tariffName())
            .jsonPath("$.accountId").isEqualTo(createdSavingAccount.accountId());
    }

    @AfterEach
    void cleanUp() {
        savingsAccountRepository.deleteAll();
    }
}
