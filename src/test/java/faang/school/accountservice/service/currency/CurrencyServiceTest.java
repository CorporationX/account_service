package faang.school.accountservice.service.currency;

import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.exception.currency.CurrencyNotFoundException;
import faang.school.accountservice.repository.currency.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    @Mock
    private CurrencyRepository currencyRepository;
    @InjectMocks
    private CurrencyService currencyService;
    private Currency currency;

    @BeforeEach
    public void setUp() {
        currency = new Currency();
        currency.setId(UUID.randomUUID());
    }
    @Test
    public void testGetCurrencyById_successfully() {
        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.of(currency));

        Currency returncurrency = currencyService.getCurrencyById(currency.getId());

        verify(currencyRepository, times(1)).findById(currency.getId());
        assertEquals(currency.getId(), returncurrency.getId());
    }

    @Test
    public void testGetCurrencyById_currencyNotFound() {
        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () -> currencyService.getCurrencyById(currency.getId()));
        verify(currencyRepository, times(1)).findById(currency.getId());
    }
}
