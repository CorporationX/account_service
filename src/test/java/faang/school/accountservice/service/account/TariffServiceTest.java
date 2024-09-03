package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.mapper.account.TariffMapper;
import faang.school.accountservice.model.account.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffMapper tariffMapper;
    @Mock
    private TariffRepository tariffRepository;
    @InjectMocks
    private TariffService tariffService;

    @Test
    void createTariff_ShouldCreateAndReturnTariffDto() {
        TariffDto inputDto = new TariffDto(null, "Test Tariff", 10.0);
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Test Tariff");
        tariff.setRateHistory("[10.0]");
        TariffDto outputDto = new TariffDto(1L, "Test Tariff", 10.0);

        when(tariffMapper.toEntity(inputDto)).thenReturn(tariff);
        when(tariffRepository.save(tariff)).thenReturn(tariff);
        when(tariffMapper.toDto(tariff)).thenReturn(outputDto);

        TariffDto result = tariffService.createTariff(inputDto);

        assertThat(result).isEqualTo(outputDto);
        verify(tariffMapper).toEntity(inputDto);
        verify(tariffRepository).save(tariff);
        verify(tariffMapper).toDto(tariff);
    }

    @Test
    void updateTariffRate_ShouldUpdateAndReturnTariffDto() throws Exception {
        Long tariffId = 1L;
        TariffDto updateDto = new TariffDto(tariffId, "Updated Tariff", 15.0);
        Tariff existingTariff = new Tariff();
        existingTariff.setId(tariffId);
        existingTariff.setName("Existing Tariff");
        existingTariff.setRateHistory("[10.0]");
        Tariff updatedTariff = new Tariff();
        updatedTariff.setId(tariffId);
        updatedTariff.setName("Updated Tariff");
        updatedTariff.setRateHistory("[10.0,15.0]");

        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(existingTariff));
        doNothing().when(tariffMapper).update(updateDto, existingTariff);
        when(tariffRepository.save(existingTariff)).thenReturn(updatedTariff);
        when(tariffMapper.toDto(updatedTariff)).thenReturn(updateDto);

        TariffDto result = tariffService.updateTariff(tariffId, updateDto);

        assertThat(result).isEqualTo(updateDto);
        verify(tariffRepository).findById(tariffId);
        verify(tariffMapper).update(updateDto, existingTariff);
        verify(tariffRepository).save(existingTariff);
        verify(tariffMapper).toDto(updatedTariff);
    }

    @Test
    void updateTariff_ShouldThrowEntityNotFoundException() {
        Long tariffId = 1L;
        TariffDto updateDto = new TariffDto(tariffId, "Updated Tariff", 15.0);

        when(tariffRepository.findById(tariffId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffService.updateTariff(tariffId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tariff not found");

        verify(tariffRepository).findById(tariffId);
        verifyNoInteractions(tariffMapper);
        verify(tariffRepository, never()).save(any());
    }

    @Test
    void getAllTariffs_ShouldReturnListOfTariffDto() {
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Test Tariff");
        tariff.setRateHistory("[10.0]");
        List<Tariff> tariffs = List.of(tariff);
        TariffDto tariffDto = new TariffDto(1L, "Test Tariff", 10.0);
        List<TariffDto> expectedDtos = List.of(tariffDto);

        when(tariffRepository.findAll()).thenReturn(tariffs);
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        List<TariffDto> result = tariffService.getAllTariffs();

        assertThat(result).isEqualTo(expectedDtos);
        verify(tariffRepository).findAll();
        verify(tariffMapper).toDto(tariff);
    }

    @Test
    void getAllTariffs_ShouldReturnEmptyListWhenNoTariffs() {
        when(tariffRepository.findAll()).thenReturn(List.of());

        List<TariffDto> result = tariffService.getAllTariffs();

        assertThat(result).isEmpty();
        verify(tariffRepository).findAll();
        verifyNoInteractions(tariffMapper);
    }
}