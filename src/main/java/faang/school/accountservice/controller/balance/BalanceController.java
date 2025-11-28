package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.ChangedBalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.dto.exception.ErrorResponse;
import faang.school.accountservice.service.balance.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Balances",
        description = "Managing account balances"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/balances")
public class BalanceController {
    private final BalanceService balanceService;

    @Operation(
            summary = "Create a new balance record",
            description = "Create a new balance record of the account",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Balance successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid request data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\":"
                                            + " \"Account not found\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<BalanceDto> create(
            @Parameter(description = "Data for creating a new record of the balance")
            @Valid
            @RequestBody
            CreateBalanceDto createBalanceDto) {
        BalanceDto balanceDto = balanceService.create(createBalanceDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(balanceDto);
    }

    @Operation(
            summary = "Update balance",
            description = "Update balance by it's Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Balance has been updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Account not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Account not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Balance record not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Balance not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PutMapping("/{balanceId}")
    public BalanceDto updateBalance(
            @Parameter(description = "Id of the balance to update", example = "1")
            @PathVariable
            long balanceId,
            @Parameter(description = "Updating data for balance")
            @Valid
            @RequestBody
            UpdateBalanceDto updateBalanceDto) {
        return balanceService.update(balanceId, updateBalanceDto);
    }

    @Operation(
            summary = "Get balance",
            description = "Get balance by it's Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Balance has been found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Balance record not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Balance not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @GetMapping("/{balanceId}")
    public BalanceDto getBalanceById(
            @Parameter(description = "Balance Id", example = "1")
            @PathVariable
            long balanceId) {
        return balanceService.getBalanceById(balanceId);
    }

    @Operation(
            summary = "Update balance - make authorization",
            description = "Update balance - make authorization",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Balance has been updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Balance record not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Balance not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - incorrect amount",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Amount must be positive\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PatchMapping("/{balanceId}/authorize")
    public BalanceDto authorize(
            @Parameter(description = "Balance Id", example = "1")
            @PathVariable
            long balanceId,
            @Parameter(description = "Data for creating an authorization")
            @Valid
            @RequestBody
            ChangedBalanceDto authorizeBalanceDto) {
        return balanceService.authorize(balanceId, authorizeBalanceDto);
    }

    @Operation(
            summary = "Update balance - confirm authorization",
            description = "Update balance - confirm authorization",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Balance has been updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Balance record not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Balance not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - insufficient balance",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Insufficient Balance\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PatchMapping("/{balanceId}/confirm")
    public BalanceDto confirmAuthorization(
            @Parameter(description = "Balance Id", example = "1")
            @PathVariable
            long balanceId,
            @Parameter(description = "Data for confirming an authorization")
            @Valid
            @RequestBody
            ChangedBalanceDto confirmBalanceDto) {
        return balanceService.confirm(balanceId, confirmBalanceDto);
    }

    @Operation(
            summary = "Update balance - release authorization",
            description = "Update balance - release authorization",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Balance has been updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BalanceDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Balance record not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Balance not found\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - insufficient balance",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Insufficient Balance\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PatchMapping("/{balanceId}/release")
    public BalanceDto releaseAuthorization(
            @Parameter(description = "Balance Id", example = "1")
            @PathVariable
            long balanceId,
            @Parameter(description = "Data for releasing an authorization")
            @Valid
            @RequestBody
            ChangedBalanceDto releaseBalanceDto) {
        return balanceService.release(balanceId, releaseBalanceDto);
    }
}