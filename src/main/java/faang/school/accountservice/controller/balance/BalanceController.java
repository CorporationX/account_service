package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.service.balance.BalanceService;
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
@RequestMapping("/api/v1/balance")
public class BalanceController {

  private final BalanceService balanceService;

  @PostMapping("/add")
  public BalanceDto addBalance(@RequestHeader("x-user-id") Long userId,
      @Valid @RequestBody BalanceCreateDto dto) {
    return balanceService.create(userId, dto);
  }

  @PutMapping("/update")
  public BalanceDto updateBalance(@RequestHeader("x-user-id") Long userId,
      @Valid @RequestBody PaymentDto dto) {
    return balanceService.update(userId, dto);
  }

  @GetMapping("/{id}")
  public BalanceDto getBalanceById(@RequestHeader("x-user-id") Long userId,
      @Valid @PathVariable("id") Long id) {
    return balanceService.getBalanceById(userId, id);
  }

}
