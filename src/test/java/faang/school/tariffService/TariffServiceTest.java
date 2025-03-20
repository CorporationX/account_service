package faang.school.tariffService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.accountservice.dto.tariff.HistoryDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.TariffService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest {
    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private TariffService tariffService;

    private final TariffType testTariffName = TariffType.BASE;
    private final double testRate = 5.0;
    private Tariff existingTariff;

    @BeforeEach
    void setUp() {
        existingTariff = Tariff.builder()
            .name(testTariffName)
            .rate(3.0)
            .createdAt(LocalDateTime.now().minusDays(1))
            .history(new ArrayList<>())
            .build();
    }

    @Test
    void create_WhenTariffNotExists_ShouldCreateNewTariff() {
        TariffRequestDto request = new TariffRequestDto(testTariffName, testRate);
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.empty());
        when(tariffRepository.save(any(Tariff.class))).thenAnswer(inv -> inv.getArgument(0));

        Tariff result = tariffService.create(request);

        assertThat(result.getName()).isEqualTo(testTariffName);
        assertThat(result.getRate()).isEqualTo(testRate);
        assertThat(result.getHistory()).isEmpty();
        verify(tariffRepository).save(any(Tariff.class));
    }

    @Test
    void create_WhenTariffExists_ShouldThrowException() {
        TariffRequestDto request = new TariffRequestDto(testTariffName, testRate);
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.of(existingTariff));

        assertThatThrownBy(() -> tariffService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void update_WhenTariffExists_ShouldUpdateTariffAndAddHistory() {
        double newRate = 7.0;
        TariffUpdateDto updateDto = new TariffUpdateDto(testTariffName, newRate);

        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.of(existingTariff));
        when(tariffRepository.save(any(Tariff.class))).thenAnswer(inv -> inv.getArgument(0));

        Tariff result = tariffService.update(updateDto);

        assertThat(result.getRate()).isEqualTo(newRate);
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getHistory()).hasSize(1);

        HistoryDto historyEntry = result.getHistory().get(0);
        assertThat(historyEntry.oldRate()).isEqualTo(String.format("%.2f%%", 3.0));
        assertThat(historyEntry.activeFrom()).isEqualTo(existingTariff.getCreatedAt());
        verify(tariffRepository).save(existingTariff);
    }

    @Test
    void update_WhenTariffNotExists_ShouldThrowException() {
        TariffUpdateDto updateDto = new TariffUpdateDto(testTariffName, 7.0);
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffService.update(updateDto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void getTariff_WhenTariffExists_ShouldReturnTariff() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.of(existingTariff));

        Tariff result = tariffService.getTariff(testTariffName);

        assertThat(result).isEqualTo(existingTariff);
    }

    @Test
    void getTariff_WhenTariffNotExists_ShouldThrowException() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffService.getTariff(testTariffName))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void checkExistTariff_WhenTariffExists_ShouldThrowException() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.of(existingTariff));

        assertThatThrownBy(() -> tariffService.checkExistTariff(testTariffName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void checkExistTariff_WhenTariffNotExists_ShouldNotThrowException() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.empty());

        tariffService.checkExistTariff(testTariffName);
    }

    @Test
    void getExistingTariff_WhenTariffExists_ShouldReturnTariff() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.of(existingTariff));

        Tariff result = tariffService.getExistingTariff(testTariffName);

        assertThat(result).isEqualTo(existingTariff);
    }

    @Test
    void getExistingTariff_WhenTariffNotExists_ShouldThrowException() {
        when(tariffRepository.findByName(testTariffName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffService.getExistingTariff(testTariffName))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("not found");
    }
}
