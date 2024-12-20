package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balance")
public class BalanceController {

  private final BalanceService balanceService;

  @PostMapping("/add")
  public BalanceDto addBalance(@RequestHeader("x-user-id") long userId, BalanceDto dto ) {
    return null;
  }

  @PutMapping("/update")
  public BalanceDto updateBalance(@RequestHeader("x-user-id") long userId, BalanceDto dto) {
    return null;
  }

  @GetMapping("/{id}")
  public BalanceDto getBalanceById(@RequestHeader("x-user-id") long userId, @PathVariable long id) {
    return null;
  }
}
