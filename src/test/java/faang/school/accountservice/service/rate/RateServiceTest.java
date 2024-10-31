package faang.school.accountservice.service.rate;

import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.repository.rate.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @InjectMocks
    private RateService rateService;

    @Mock
    private RateRepository rateRepository;

    private static final Double INTEREST_RATE = 5.0;
    private Rate rate;

    @BeforeEach
    public void init() {
        rate = Rate.builder()
                .interestRate(INTEREST_RATE)
                .build();
    }

    @Test
    @DisplayName("Успех при получении Rate если такая Rate уже есть в БД")
    public void whenGetRateByInterestRateShouldReturnExistedEntity() {
        when(rateRepository.findByInterestRate(INTEREST_RATE)).thenReturn(Optional.of(rate));

        Rate rate = rateService.getRateByInterestRate(INTEREST_RATE);

        assertNotNull(rate);
        assertEquals(INTEREST_RATE, rate.getInterestRate());
        verify(rateRepository).findByInterestRate(INTEREST_RATE);
    }

    @Test
    @DisplayName("Успех при получении Rate если такой Rate еще нет в БД")
    public void whenGetRateByInterestRateShouldReturnException() {
        when(rateRepository.findByInterestRate(INTEREST_RATE)).thenReturn(Optional.empty());
        when(rateRepository.save(any(Rate.class))).thenReturn(rate);

        Rate rate = rateService.getRateByInterestRate(INTEREST_RATE);

        assertNotNull(rate);
        assertEquals(INTEREST_RATE, rate.getInterestRate());
        verify(rateRepository).save(any(Rate.class));
    }
}