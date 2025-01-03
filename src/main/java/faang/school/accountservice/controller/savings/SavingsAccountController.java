package faang.school.accountservice.controller.savings;

import faang.school.accountservice.dto.savings.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.service.savings.SavingsAccountService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class SavingsAccountController {

  private final SavingsAccountService savingsAccountService;

  @PostMapping("/add")
  public SavingsAccountResponseDto addSavingsAccount(@RequestHeader("x-user-id") Long userId,
      @Valid @RequestBody SavingsAccountCreateDto dto) {
    return savingsAccountService.add(userId, dto);
  }

  @GetMapping("/{id}")
  public SavingsAccountResponseDto getSavingsAccountById(@RequestHeader("x-user-id") Long userId,
      @PathVariable @Valid Long id) {
    return savingsAccountService.getById(userId, id);
  }

  @GetMapping("/owner/{ownerId}")
  public List<SavingsAccountResponseDto> getSavingsAccountByOwnerId(@RequestHeader("x-user-id") Long userId,
      @PathVariable @Valid Long ownerId) {
    return savingsAccountService.getSavingsDtoByOwner(userId, ownerId);
  }

}
