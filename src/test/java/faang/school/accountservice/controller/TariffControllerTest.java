package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.dto.TariffUpdateRequest;
import faang.school.accountservice.service.TariffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = TariffController.class)
public class TariffControllerTest {

    private final String typeName = "tariff";
    private final String rate = BigDecimal.valueOf(5.00).toString();

    @MockBean
    private TariffService tariffService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPositiveAddTariff() throws Exception {
        TariffResponse response = createResponse(rate);
        when(tariffService.addTariff(typeName, new BigDecimal(rate))).thenReturn(response);

        mockMvc.perform(post("/tariffs")
                        .param("typeName", typeName)
                        .param("rate", rate))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }

    @Test
    void testPositiveUpdateTariff() throws Exception {
        TariffUpdateRequest request = createRequest(rate);
        tariffService.updateTariff(request);

        mockMvc.perform(put("/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetAllTariffs() throws Exception {
        List<TariffResponse> responses = List.of(
                createResponse(rate), createResponse(rate)
        );
        when(tariffService.getAllTariffs()).thenReturn(responses);

        mockMvc.perform(get("/tariffs"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responses)));
    }

    private TariffUpdateRequest createRequest(String rate) {
        return TariffUpdateRequest.builder()
                .id(1L)
                .typeName(typeName)
                .rate(new BigDecimal(rate))
                .build();
    }

    private TariffResponse createResponse(String rate) {
        return TariffResponse.builder()
                .typeName(typeName)
                .activeRate(rate)
                .build();
    }
}
