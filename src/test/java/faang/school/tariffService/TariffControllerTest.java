package faang.school.tariffService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AccountServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TariffControllerTest extends BaseIntegrationTest {

    @Autowired
    private TariffRepository tariffRepository;

    private static final String TARIFF_URL = "/api/v1/tariff";
    private static final TariffType TEST_TARIFF = TariffType.BASE;
    private static final double TEST_RATE = 15.0;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTariff() throws Exception {
        TariffResponseDto createdTariff = webTestClient.post()
            .uri(TARIFF_URL)
            .bodyValue(new TariffRequestDto(TariffType.ENTERPRISE, TEST_RATE))
            .exchange()
            .expectStatus().isOk()
            .expectBody(TariffResponseDto.class)
            .returnResult()
            .getResponseBody();

        assertThat(createdTariff).isNotNull();
        assertThat(createdTariff.name().toString()).isEqualTo("ENTERPRISE");

    }

    @Test
    void getTariff() throws Exception {
        TariffResponseDto createdTariff = webTestClient.post()
            .uri(TARIFF_URL)
            .bodyValue(new TariffRequestDto(TEST_TARIFF, TEST_RATE))
            .exchange()
            .expectStatus().isOk()
            .expectBody(TariffResponseDto.class)
            .returnResult()
            .getResponseBody();

        webTestClient.get()
            .uri(TARIFF_URL + "/{tariffName}", TEST_TARIFF.name())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo(TEST_TARIFF.name())
            .jsonPath("$.rate").isEqualTo(String.format("%.2f%%", TEST_RATE));
    }

    @Test
    void updateTariff() throws Exception {
        TariffRequestDto createRequest = new TariffRequestDto(TariffType.PREMIUM, 10.0);
        webTestClient.post()
            .uri(TARIFF_URL)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isOk();

        TariffUpdateDto updateRequest = new TariffUpdateDto(TariffType.PREMIUM, 15.0);
        webTestClient.patch()
            .uri(TARIFF_URL)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.rate").isEqualTo(String.format("%.2f%%", TEST_RATE))
            .jsonPath("$.history[0].oldRate").isEqualTo(String.format("%.2f%%", 10.0));
    }

    @AfterEach
    void cleanUp() {
        tariffRepository.deleteAll();
    }
}
