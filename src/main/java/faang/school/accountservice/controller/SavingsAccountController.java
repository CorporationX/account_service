package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/savings-account")
@RequiredArgsConstructor
public class SavingsAccountController {

    @Operation(summary = "Create account", description = "Create account in DB")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public SavingsAccountDto openSavingAccount(@RequestBody SavingsAccountDto savingsAccountDto) {


        return null;
    }

}
