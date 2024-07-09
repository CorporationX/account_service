package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffRateChangeRequestDto;
import faang.school.accountservice.model.TariffRateChangeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TariffRateChangeRequestMapperTest {

    @InjectMocks
    private TariffRateChangeRequestMapperImpl mapper;

    @Test
    void shouldMapRequestToDto() {
        TariffRateChangeRequest request = TariffRateChangeRequest.builder()
                .id(1L)
                .tariffId(2L)
                .newRate(BigDecimal.valueOf(0.05))
                .requestedAt(LocalDateTime.now())
                .changeDate(LocalDateTime.now().plusDays(7))
                .status(TariffRateChangeRequest.RequestStatus.PENDING)
                .build();

        TariffRateChangeRequestDto dto = mapper.toDto(request);

        assertEquals(request.getTariffId(), dto.getTariffId());
        assertEquals(request.getNewRate(), dto.getNewRate());
        assertEquals(request.getChangeDate(), dto.getChangeDate());
    }
}