package faang.school.accountservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = TariffController.class)
class TariffControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TariffService service;

    private static final Long TARIFF_ID = 1L;
    private static final String BASE_TARIFF = "BASE";
    private static final String NEW_TARIFF_TYPE = "PRO";

    @Test
    @DisplayName("200 ОК - POST /v1/tariffs")
    void positive_shouldCallCreateTariff() throws Exception {
        TariffDto tariffDto = createTariffDto(null, NEW_TARIFF_TYPE);
        TariffDto expected = createTariffDto(TARIFF_ID, NEW_TARIFF_TYPE);
        when(service.create(any(TariffDto.class))).thenReturn(expected);

        mockMvc.perform(post("/v1/tariffs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(tariffDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(expected), true))
                .andExpect(jsonPath("$.id").value(TARIFF_ID));

        verify(service, times(1)).create(tariffDto);
    }

    @Test
    @DisplayName("200 ОК - PUT /v1/tariffs")
    void positive_shouldCallUpdate() throws Exception {
        TariffDto tariffDto = createTariffDto(TARIFF_ID, BASE_TARIFF);
        TariffDto expected = createTariffDto(TARIFF_ID, NEW_TARIFF_TYPE);
        when(service.update(any(TariffDto.class))).thenReturn(expected);

        mockMvc.perform(put("/v1/tariffs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(tariffDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(expected), true))
                .andExpect(jsonPath("$.id").value(TARIFF_ID))
                .andExpect(jsonPath("$.type").value(NEW_TARIFF_TYPE));

        verify(service, times(1)).update(tariffDto);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/tariffs")
    void positive_shouldFindAll() throws Exception {
        List<TariffDto> expected = List.of(createTariffDto(TARIFF_ID, BASE_TARIFF));
        when(service.findAll()).thenReturn(expected);

        mockMvc.perform(get("/v1/tariffs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(expected), true))
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(service, times(1)).findAll();
    }

    private static Stream<Arguments> provideNotValidTariff() {
        return Stream.of(
                Arguments.of(new TariffDto(TARIFF_ID, null, BigDecimal.TEN, null, null)),
                Arguments.of(new TariffDto(TARIFF_ID, BASE_TARIFF, null, null, null)));
    }

    @ParameterizedTest
    @MethodSource("provideNotValidTariff")
    @DisplayName("400 Bad Request - POST /v1/tariffs - поля тела не валидны")
    void negative_whenDataDtoNotValid_returns400BadRequest(TariffDto tariffDto) throws Exception {
        mockMvc.perform(post("/v1/tariffs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(tariffDto)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(TariffDto.class));
    }

    // -----------------------------

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private TariffDto createTariffDto(Long id, String type) {
        return TariffDto.builder()
                .id(id)
                .type(type)
                .currentRate(BigDecimal.TEN)
                .build();
    }
}