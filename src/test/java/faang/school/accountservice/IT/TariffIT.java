package faang.school.accountservice.IT;

import faang.school.accountservice.controller.ApiPath;
import faang.school.accountservice.dto.account.TariffDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TariffIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("account")
            .withUsername("account")
            .withPassword("account");

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.set("x-user-id", "1");
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testCreateTariff() {
        TariffDto tariffDto = new TariffDto(null, "Test Tariff", 10.0);
        HttpEntity<TariffDto> request = new HttpEntity<>(tariffDto, headers);
        ResponseEntity<TariffDto> response = restTemplate.exchange(
                String.format("http://localhost:%d%s", port, ApiPath.TARIFF_PATH),
                HttpMethod.POST,
                request,
                TariffDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test Tariff");
        assertThat(response.getBody().currentRate()).isEqualTo(10.0);
    }

    @Test
    void testUpdateTariff() {
        TariffDto tariffDto = new TariffDto(null, "Initial Tariff", 10.0);
        HttpEntity<TariffDto> createRequest = new HttpEntity<>(tariffDto, headers);
        ResponseEntity<TariffDto> createResponse = restTemplate.exchange(
                String.format("http://localhost:%d%s", port, ApiPath.TARIFF_PATH),
                HttpMethod.POST,
                createRequest,
                TariffDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long tariffId = createResponse.getBody().id();

        TariffDto updateDto = new TariffDto(tariffId, "Updated Tariff", 15.0);
        HttpEntity<TariffDto> updateRequest = new HttpEntity<>(updateDto, headers);
        ResponseEntity<TariffDto> updateResponse = restTemplate.exchange(
                String.format("http://localhost:%d%s/%d", port, ApiPath.TARIFF_PATH, tariffId),
                HttpMethod.PUT,
                updateRequest,
                TariffDto.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().id()).isEqualTo(tariffId);
        assertThat(updateResponse.getBody().name()).isEqualTo("Updated Tariff");
        assertThat(updateResponse.getBody().currentRate()).isEqualTo(15.0);
    }

    @Test
    void testGetAllTariffs() {
        TariffDto tariff1 = new TariffDto(null, "Tariff 1", 10.0);
        TariffDto tariff2 = new TariffDto(null, "Tariff 2", 20.0);
        HttpEntity<TariffDto> request1 = new HttpEntity<>(tariff1, headers);
        HttpEntity<TariffDto> request2 = new HttpEntity<>(tariff2, headers);
        restTemplate.exchange(String.format("http://localhost:%d%s", port, ApiPath.TARIFF_PATH), HttpMethod.POST, request1, TariffDto.class);
        restTemplate.exchange(String.format("http://localhost:%d%s", port, ApiPath.TARIFF_PATH), HttpMethod.POST, request2, TariffDto.class);

        HttpEntity<?> getRequest = new HttpEntity<>(headers);
        ResponseEntity<TariffDto[]> response = restTemplate.exchange(
                String.format("http://localhost:%d%s", port, ApiPath.TARIFF_PATH),
                HttpMethod.GET,
                getRequest,
                TariffDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(Arrays.stream(response.getBody())
                .map(TariffDto::name))
                .contains("Tariff 1", "Tariff 2");
    }
}
