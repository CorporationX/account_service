package faang.school.accountservice.controller.tariff;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.service.tariff.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TariffControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private TariffController tariffController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tariffController).build();
    }

    @Test
    void testGetAllTariffs() throws Exception {
        Mockito.when(tariffService.getAllTariffs())
                .thenReturn(List.of(new TariffDto(1L, "test", BigDecimal.valueOf(0.16), "{}")));

        mockMvc.perform(get("/tariffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[0].rate").value("0.16"))
                .andExpect(jsonPath("$[0].rateHistory").value("{}"));
    }

    @Test
    void testGetById() throws Exception {
        Mockito.when(tariffService.findById(1L))
                .thenReturn(new TariffDto(1L, "test", BigDecimal.valueOf(0.16), "{}"));

        mockMvc.perform(get("/tariffs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.rate").value("0.16"))
                .andExpect(jsonPath("$.rateHistory").value("{}"));
    }

    @Test
    void testCreateTariff() throws Exception {
        Mockito.when(tariffService.createTariff(any()))
                .thenReturn(new TariffDto(1L, "test", BigDecimal.valueOf(0.16), "{}"));

        TariffCreateDto createDto = TariffCreateDto.builder()
                .name("test")
                .rate(BigDecimal.valueOf(0.16))
                .build();

        mockMvc.perform(post("/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.rate").value("0.16"))
                .andExpect(jsonPath("$.rateHistory").value("{}"));
    }

    @Test
    void testNotValidaCreateTariff() throws Exception {
        TariffCreateDto createDto = TariffCreateDto.builder()
                .rate(BigDecimal.valueOf(0.16))
                .build();

        mockMvc.perform(post("/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTariff() throws Exception {
        Mockito.when(tariffService.updateTariff(any()))
                .thenReturn(new TariffDto(1L, "test", BigDecimal.valueOf(0.16), "{}"));

        TariffUpdateDto createDto = TariffUpdateDto.builder()
                .id(1L)
                .rate(BigDecimal.valueOf(0.16))
                .build();

        mockMvc.perform(patch("/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.rate").value("0.16"))
                .andExpect(jsonPath("$.rateHistory").value("{}"));
    }

    @Test
    void testNotValidUpdateTariff() throws Exception {
        TariffUpdateDto createDto = TariffUpdateDto.builder()
                .rate(BigDecimal.valueOf(0.16))
                .build();

        mockMvc.perform(patch("/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isBadRequest());
    }
}
