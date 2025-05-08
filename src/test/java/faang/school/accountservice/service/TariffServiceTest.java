package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.dto.TariffUpdateRequest;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.exception.TariffDuplicateException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest {

    private final String firstTypeName = "tariff";
    private final BigDecimal rate = BigDecimal.valueOf(5);

    @InjectMocks
    private TariffService tariffService;

    @Mock
    private TariffRepository tariffRepository;

    @Spy
    private TariffMapper tariffMapper;

    @BeforeEach
    void setUp() {
        tariffService = new TariffService(tariffRepository, tariffMapper);
    }

    @Test
    void testNegativeAddWhenTariffExists() {
        when(tariffRepository.existsByTypeName(firstTypeName)).thenReturn(true);

        assertThrows(TariffDuplicateException.class, () -> tariffService.addTariff(firstTypeName, rate));
    }

    @Test
    void testPositiveAddTariff() {
        TariffResponse response = createResponse(firstTypeName);
        Tariff tariff = createTariff(firstTypeName);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(response);

        TariffResponse result = tariffService.addTariff(firstTypeName, rate);

        assertEquals(response.typeName(), result.typeName());
        assertEquals(response.activeRate(), result.activeRate());
    }

    @Test
    void testNegativeUpdateWhenTariffNotExists() {
        TariffUpdateRequest request = createRequest();

        assertThrows(TariffNotFoundException.class, () -> tariffService.updateTariff(request));
    }

    @Test
    void testPositiveUpdateTariff() {
        TariffUpdateRequest request = createRequest();
        Tariff tariff = createTariff(firstTypeName);
        when(tariffRepository.findById(request.id())).thenReturn(Optional.of(tariff));

        tariffService.updateTariff(request);

        verify(tariffRepository, times(1)).save(tariff);
    }

    @Test
    void testPositiveGetAllTariffs() {
        String secondTypeName = "tariff 2";
        List<Tariff> tariffs = List.of(
                createTariff(firstTypeName), createTariff(secondTypeName)
        );
        List<TariffResponse> responses = List.of(
                createResponse(firstTypeName), createResponse(secondTypeName)
        );
        when(tariffRepository.findAll()).thenReturn(tariffs);
        when(tariffMapper.toDtoList(tariffs)).thenReturn(responses);

        List<TariffResponse> result = tariffService.getAllTariffs();

        assertEquals(responses, result);
    }

    private Tariff createTariff(String typeName) {
        return Tariff.builder()
                .typeName(typeName)
                .rates(new ArrayList<>())
                .build();
    }

    private TariffResponse createResponse(String typeName) {
        return TariffResponse.builder()
                .typeName(typeName)
                .build();
    }

    private TariffUpdateRequest createRequest() {
        return TariffUpdateRequest.builder()
                .id(1L)
                .typeName("another tariff")
                .rate(BigDecimal.valueOf(10))
                .build();
    }
}
