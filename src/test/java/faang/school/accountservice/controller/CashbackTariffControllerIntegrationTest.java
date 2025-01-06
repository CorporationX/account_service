package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.dto.cashbackdto.CashbackMappingType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.handler.GlobalExceptionHandler;
import faang.school.accountservice.service.CashbackTariffService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashbackTariffController.class)
@ContextConfiguration(classes = {CashbackTariffController.class, GlobalExceptionHandler.class})
public class CashbackTariffControllerIntegrationTest {

    private final static String CREATE_URL = "/tariffs/create";
    private final static String GET_URL = "/tariffs/{id}";
    private final static String ADD_MAPPING_URL = "/tariffs/{id}/mappings";
    private final static String UPDATE_MAPPING_URL = "/tariffs/{id}/mappings";
    private final static String DELETE_MAPPING_URL = "/tariffs/{id}/mappings";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CashbackTariffService cashbackTariffService;

    @Test
    void createTariff_Success() throws Exception {
        Long expectedTariffId = 1L;
        Mockito.when(cashbackTariffService.createTariff()).thenReturn(expectedTariffId);

        mockMvc.perform(post(CREATE_URL))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedTariffId.toString()));
    }

    @Test
    void getTariffById_Success() throws Exception {
        Long tariffId = 1L;
        CashbackTariffDto tariffDto = CashbackTariffDto.builder()
                .id(tariffId)
                .createdAt(LocalDateTime.now())
                .operationMappings(Collections.emptyList())
                .merchantMappings(Collections.emptyList())
                .build();
        Mockito.when(cashbackTariffService.getTariffById(tariffId)).thenReturn(tariffDto);

        mockMvc.perform(get(GET_URL, tariffId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tariffDto)));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }
    }

    @Test
    void getTariffById_NotFound() throws Exception {
        Long tariffId = 1L;
        Mockito.when(cashbackTariffService.getTariffById(tariffId))
                .thenThrow(new EntityNotFoundException("Tariff not found with id " + tariffId));

        mockMvc.perform(get("/tariff/{id}", tariffId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMapping_Success() throws Exception {
        Long tariffId = 1L;
        CashbackMappingDto mappingDto = new CashbackMappingDto(
                CashbackMappingType.OPERATION,
                "OPERATION_KEY",
                new BigDecimal("5")
        );

        mockMvc.perform(post(ADD_MAPPING_URL, tariffId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(mappingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMapping_Success() throws Exception {
        Long tariffId = 1L;
        CashbackMappingDto mappingDto = new CashbackMappingDto(
                CashbackMappingType.OPERATION,
                "OPERATION_KEY",
                new BigDecimal("10")
        );

        mockMvc.perform(put(UPDATE_MAPPING_URL, tariffId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(mappingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMapping_Success() throws Exception {
        Long tariffId = 1L;
        String mappingKey = "OPERATION_KEY";
        Mockito.doNothing().when(cashbackTariffService).deleteMapping(tariffId, mappingKey);

        mockMvc.perform(delete(DELETE_MAPPING_URL, tariffId)
                        .param("mappingKey", mappingKey))
                .andExpect(status().isOk());
    }
}