package faang.school.accountservice.controller.transaction;

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

    @PostMapping
    public void createTransaction(@RequestBody @Valid TransactionDtoToCreate dto) {
        return transactionService.createTransaction(dto);
    }

    @GetMapping("/{transactionId}")
    public TransactionDto getTransaction(@PathVariable("transactionId") long id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping("/cancel/{transactionId}")
    public void cancelTransaction(@PathVariable("transactionId") long id) {
        return transactionService.cancelTransaction(id);
    }
}

