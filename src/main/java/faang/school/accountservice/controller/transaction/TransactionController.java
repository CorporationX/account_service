package faang.school.accountservice.controller.transaction;

import faang.school.accountservice.config.context.user.UserContext;
import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;
import faang.school.accountservice.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserContext userContext;

    @PostMapping
    public TransactionDto createTransaction(@RequestBody @Valid TransactionDtoToCreate dto) {
        Long userId = userContext.getUserId();
        return transactionService.createTransaction(userId, dto);
    }

    @GetMapping("/{transactionId}")
    public TransactionDto getTransaction(@PathVariable("transactionId") long id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping("/cancel/{transactionId}")
    public TransactionDto cancelTransaction(@PathVariable("transactionId") long id) {
        Long userId = userContext.getUserId();
        return transactionService.cancelTransaction(id);
    }
}

