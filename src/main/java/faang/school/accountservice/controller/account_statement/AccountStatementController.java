package faang.school.accountservice.controller.account_statement;

import faang.school.accountservice.config.context.user.UserContext;
import faang.school.accountservice.dto.account_statement.AccountStatementDto;
import faang.school.accountservice.dto.account_statement.AccountStatementDtoToCreate;
import faang.school.accountservice.service.account_statement.AccountStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("accountStatement")
@RequiredArgsConstructor
@Tag(name = "AccountStatement")
public class AccountStatementController {

    private final AccountStatementService accountStatementService;
    private final UserContext userContext;

    @Operation(summary = "Get account statement PDF file, not saved in DB, just to read.")
    @GetMapping
    public InputStream getAccountPaymentHistory(
            @RequestBody AccountStatementDtoToCreate dto) {
        return accountStatementService.getHistory(dto, userContext.getUserId());
    }

    @Operation(summary = "Get account statement PDF file from Amazon S3, previously saved.")
    @GetMapping("/{key}")
    public InputStream getAccountStatementFile(
            @PathVariable("key") String key) {
        return accountStatementService.getFile(key, userContext.getUserId());
    }

    @Operation(summary = "Upload account statement PDF file.")
    @PostMapping
    public void uploadAccountStatementFile(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            throw new IllegalArgumentException("File must be a PDF.");
        }
        accountStatementService.createFileAndUpload(file, userContext.getUserId());
    }

    @Operation(summary = "Delete account statement PDF file.")
    @DeleteMapping("/{key}")
    public void deleteAccountStatementFile(
            @PathVariable("key") String key) {
        accountStatementService.deleteFile(key, userContext.getUserId());
    }

    @Operation(summary = "Download account statement PDF file from Amazon S3.")
    @GetMapping("/download/{key}")
    public InputStream downloadAccountStatementFile(
            @PathVariable("key") String key) {
        return accountStatementService.downloadFile(key, userContext.getUserId());
    }
}
