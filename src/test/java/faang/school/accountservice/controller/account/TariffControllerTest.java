package faang.school.accountservice.controller.account;

import faang.school.accountservice.controller.ApiPath;
import faang.school.accountservice.controller.BaseControllerTest;
import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.service.account.TariffService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffController.class)
class TariffControllerTest extends BaseControllerTest {

    @MockBean
    private TariffService tariffService;

    private TariffDto tariffDto;

    @BeforeEach
    void setUp() {
        tariffDto = new TariffDto(1L, "Test Tariff", 10.0);
    }

    @Test
    void createTariff_ShouldReturnCreatedTariff() throws Exception {
        when(tariffService.createTariff(any(TariffDto.class))).thenReturn(tariffDto);

        mockMvc.perform(post(ApiPath.TARIFF_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE)
                        .content(objectMapper.writeValueAsString(tariffDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Tariff"))
                .andExpect(jsonPath("$.currentRate").value(10.0));
    }

    @Test
    void updateTariff_ShouldReturnUpdatedTariff() throws Exception {
        TariffDto updatedTariffDto = new TariffDto(1L, "Updated Tariff", 15.0);
        when(tariffService.updateTariff(eq(1L), any(TariffDto.class))).thenReturn(updatedTariffDto);

        mockMvc.perform(put(ApiPath.TARIFF_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE)
                        .content(objectMapper.writeValueAsString(updatedTariffDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Tariff"))
                .andExpect(jsonPath("$.currentRate").value(15.0));
    }

    @Test
    void updateTariff_ShouldReturnNotFoundForNonExistingTariff() throws Exception {
        when(tariffService.updateTariff(eq(1L), any(TariffDto.class)))
                .thenThrow(new EntityNotFoundException("Tariff not found"));

        mockMvc.perform(put(ApiPath.TARIFF_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE)
                        .content(objectMapper.writeValueAsString(tariffDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTariffs_ShouldReturnListOfTariffs() throws Exception {
        List<TariffDto> tariffs = Arrays.asList(
                new TariffDto(1L, "Tariff 1", 10.0),
                new TariffDto(2L, "Tariff 2", 15.0)
        );
        when(tariffService.getAllTariffs()).thenReturn(tariffs);

        mockMvc.perform(get(ApiPath.TARIFF_PATH)
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Tariff 1"))
                .andExpect(jsonPath("$[0].currentRate").value(10.0))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Tariff 2"))
                .andExpect(jsonPath("$[1].currentRate").value(15.0));
    }

    @Test
    void getTariffs_ShouldReturnEmptyListWhenNoTariffs() throws Exception {
        when(tariffService.getAllTariffs()).thenReturn(List.of());

        mockMvc.perform(get(ApiPath.TARIFF_PATH)
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}