package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Creating a payment account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment account created"),
            @ApiResponse(responseCode = "500", description = "Error occurred when creating the payment account")
    })
    @PostMapping
    public AccountDto openAccount(@RequestBody @Valid AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @Operation(summary = "Getting a payment account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment account received"),
            @ApiResponse(responseCode = "404", description = "Payment account not found"),
            @ApiResponse(responseCode = "500", description = "Error occurred when receiving the payment account")
    })
    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable long id) {
        return accountService.getAccount(id);
    }

    @Operation(
            summary = "Update an existing payment account by ID",
            description = "Updates the status of a payment account to one of the specified statuses: " +
                    "ACTIVE, FROZEN, CLOSED, or BLOCKED.\n" + "This operation allows for changing the operational " +
                    "state of the account based on required business rules or account conditions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment account updated"),
            @ApiResponse(responseCode = "404", description = "Payment account not found"),
            @ApiResponse(responseCode = "500", description = "Error occurred when updated the payment account")
    })
    @PutMapping("/{id}")
    public AccountDto updateStatusAccount(@PathVariable long id, AccountStatus accountStatus) {
        return accountService.updateStatusAccount(id, accountStatus);
    }

    @Operation(summary = "Close a payment account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment account closed"),
            @ApiResponse(responseCode = "404", description = "Payment account not found"),
            @ApiResponse(responseCode = "500", description = "Error occurred when closed the payment account")
    })
    @PutMapping("/close/{id}")
    public AccountDto closeAccount(@PathVariable long id) {
        return accountService.closeAccount(id);
    }

}