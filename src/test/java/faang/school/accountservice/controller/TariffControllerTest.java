package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TariffControllerTest {

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private TariffController tariffController;

    private Long tariffId;
    private BigDecimal newRate;
    private CreateTariffRequest request;
    private TariffDto expectedDto;

    @BeforeEach
    void setUp(){
        tariffId = 1L;
        newRate = BigDecimal.valueOf(0.05);
        request = CreateTariffRequest.builder()
                .name("Test")
                .initialRate(BigDecimal.valueOf(0.05))
                .build();
        expectedDto = TariffDto.builder()
                .id(tariffId)
                .name("Test")
                .currentRate(BigDecimal.valueOf(0.05))
                .build();
    }

    @Test
    void shouldCreateTariff(){
        when(tariffService.createTariff(request)).thenReturn(expectedDto);
        TariffDto result = tariffService.createTariff(request);
        assertEquals(expectedDto, result);
    }

    @Test
    void shouldUpdateTariff(){
        ResponseEntity<Void> response = tariffController.updateTariff(tariffId, newRate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldGetTariffById(){
        when(tariffService.getTariffById(tariffId)).thenReturn(expectedDto);
        TariffDto result = tariffController.getTariffById(tariffId);
        assertEquals(expectedDto, result);
    }
}
