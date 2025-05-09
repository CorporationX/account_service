package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.TariffCreationDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.entity.Tariff;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TariffMapperTest {

    private final TariffMapper mapper = Mappers.getMapper(TariffMapper.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void toTariff_ShouldMapCreationDtoToEntityCorrectly() {
        TariffCreationDto dto = new TariffCreationDto();
        dto.setName("Premium Savings");
        dto.setInitRate(BigDecimal.valueOf(5.25));

        Tariff tariff = mapper.toTariff(dto);

        assertNotNull(tariff);
        assertEquals("Premium Savings", tariff.getName());
        assertEquals(Collections.singletonList(BigDecimal.valueOf(5.25)), tariff.getRateHistory());
        assertNotNull(tariff.getCreatedAt());
        assertNotNull(tariff.getUpdatedAt());
        assertTrue(LocalDateTime.now().minusSeconds(1).isBefore(tariff.getCreatedAt()));
        assertTrue(LocalDateTime.now().plusSeconds(1).isAfter(tariff.getCreatedAt()));
    }

    @Test
    void toTariffResponseDto_ShouldMapEntityToResponseDtoCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Standard Savings");
        tariff.setRateHistory(List.of(BigDecimal.valueOf(3.5), BigDecimal.valueOf(4.0)));
        tariff.setCreatedAt(now.minusDays(5));
        tariff.setUpdatedAt(now);

        TariffResponseDto dto = mapper.toTariffResponseDto(tariff);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Standard Savings", dto.getName());
        assertEquals(List.of(BigDecimal.valueOf(3.5), BigDecimal.valueOf(4.0)), dto.getRateHistory());
        assertEquals(now.minusDays(5).format(formatter), dto.getCreatedAt());
        assertEquals(now.format(formatter), dto.getUpdatedAt());
    }

    @Test
    void toTariffResponseDto_ShouldFormatDatesCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30);
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Test");
        tariff.setRateHistory(List.of(BigDecimal.ONE));
        tariff.setCreatedAt(dateTime);
        tariff.setUpdatedAt(dateTime);

        TariffResponseDto dto = mapper.toTariffResponseDto(tariff);

        assertEquals("15.06.2023 14:30", dto.getCreatedAt());
        assertEquals("15.06.2023 14:30", dto.getUpdatedAt());
    }

    @Test
    void toTariffResponseDtoList_ShouldMapListOfEntitiesCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Tariff tariff1 = new Tariff();
        tariff1.setId(1L);
        tariff1.setName("Tariff 1");
        tariff1.setRateHistory(List.of(BigDecimal.valueOf(1.5)));
        tariff1.setCreatedAt(now.minusDays(2));
        tariff1.setUpdatedAt(now.minusDays(1));
        Tariff tariff2 = new Tariff();
        tariff2.setId(2L);
        tariff2.setName("Tariff 2");
        tariff2.setRateHistory(List.of(BigDecimal.valueOf(2.5)));
        tariff2.setCreatedAt(now.minusDays(3));
        tariff2.setUpdatedAt(now.minusDays(2));
        List<Tariff> tariffs = List.of(tariff1, tariff2);

        List<TariffResponseDto> dtos = mapper.toTariffResponseDtoList(tariffs);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Tariff 1", dtos.get(0).getName());
        assertEquals(List.of(BigDecimal.valueOf(1.5)), dtos.get(0).getRateHistory());
        assertEquals(now.minusDays(2).format(formatter), dtos.get(0).getCreatedAt());
        assertEquals(now.minusDays(1).format(formatter), dtos.get(0).getUpdatedAt());
        assertEquals(2L, dtos.get(1).getId());
        assertEquals("Tariff 2", dtos.get(1).getName());
        assertEquals(List.of(BigDecimal.valueOf(2.5)), dtos.get(1).getRateHistory());
        assertEquals(now.minusDays(3).format(formatter), dtos.get(1).getCreatedAt());
        assertEquals(now.minusDays(2).format(formatter), dtos.get(1).getUpdatedAt());
    }

    @Test
    void toTariffResponseDto_ShouldHandleNullValues() {
        Tariff tariff = new Tariff();
        tariff.setRateHistory(Collections.emptyList());

        TariffResponseDto dto = mapper.toTariffResponseDto(tariff);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNotNull(dto.getRateHistory());
        assertTrue(dto.getRateHistory().isEmpty());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }
}