package faang.school.accountservice.mapper.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.model.account.Tariff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffMapperTest {

    @Mock
    private ObjectMapper objectMapper;
    private TariffMapper tariffMapper;

    @BeforeEach
    void setUp() {
        tariffMapper = Mappers.getMapper(TariffMapper.class);
        tariffMapper.setObjectMapper(objectMapper);
    }

    @Test
    void testToDto() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Basic");
        tariff.setRateHistory("[5.0,6.0,7.0]");

        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(Arrays.asList(5.0, 6.0, 7.0));

        TariffDto dto = tariffMapper.toDto(tariff);

        assertNotNull(dto);
        assertEquals(tariff.getId(), dto.id());
        assertEquals(tariff.getName(), dto.name());
        assertEquals(7.0, dto.currentRate());
    }

    @Test
    void testToEntity() {
        TariffDto dto = new TariffDto(1L, "Premium", 8.5);

        Tariff entity = tariffMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.name(), entity.getName());
        assertEquals("[8.5]", entity.getRateHistory());
    }

    @Test
    void testUpdate() throws Exception {
        TariffDto dto = new TariffDto(1L, "Updated", 9.0);
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Original");
        tariff.setRateHistory("[7.0,8.0]");

        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(Arrays.asList(7.0, 8.0));
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("[7.0,8.0,9.0]");

        tariffMapper.update(dto, tariff);

        assertEquals(dto.name(), tariff.getName());
        assertEquals("[7.0,8.0,9.0]", tariff.getRateHistory());
    }

    @Test
    void testExtractLastRate() throws Exception {
        String rateHistory = "[5.0,6.0,7.0]";
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(Arrays.asList(5.0, 6.0, 7.0));

        double result = tariffMapper.extractLastRate(rateHistory);

        assertEquals(7.0, result);
    }

    @Test
    void testExtractLastRateWithEmptyHistory() throws Exception {
        String rateHistory = "[]";
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(List.of());

        double result = tariffMapper.extractLastRate(rateHistory);

        assertEquals(0.0, result);
    }

    @Test
    void testExtractLastRateWithNullHistory() {
        double result = tariffMapper.extractLastRate(null);

        assertEquals(0.0, result);
    }

    @Test
    void testInitializeRateHistory() {
        String result = tariffMapper.initializeRateHistory(5.0);

        assertEquals("[5.0]", result);
    }

    @Test
    void testStringToDoubleList() throws Exception {
        String json = "[1.0,2.0,3.0]";
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(Arrays.asList(1.0, 2.0, 3.0));

        List<Double> result = tariffMapper.stringToDoubleList(json);

        assertEquals(Arrays.asList(1.0, 2.0, 3.0), result);
    }

    @Test
    void testDoubleListToString() throws Exception {
        List<Double> list = Arrays.asList(1.0, 2.0, 3.0);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("[1.0,2.0,3.0]");

        String result = tariffMapper.doubleListToString(list);

        assertEquals("[1.0,2.0,3.0]", result);
    }

}