package faang.school.accountservice.controller.savings;

import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.dto.savings.TariffRateHistoryDto;
import faang.school.accountservice.service.savings.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings/tariffs")
public class TariffController {

  private final TariffService tariffService;

  @PostMapping("/add")
  public TariffRateHistoryDto add(@RequestHeader("x-user-id") Long userId, @Valid @RequestBody TariffDto dto) {
    return tariffService.add(userId, dto);
  }

  @GetMapping("/{id}")
  public TariffRateHistoryDto getById(@RequestHeader("x-user-id") Long userId, @Valid @PathVariable Long id) {
    return tariffService.getById(userId, id);
  }

  @PutMapping("/update")
  public TariffRateHistoryDto update(@RequestHeader("x-user-id") Long userId, @Valid @RequestBody TariffDto dto) {
    return tariffService.update(userId, dto);
  }

}
