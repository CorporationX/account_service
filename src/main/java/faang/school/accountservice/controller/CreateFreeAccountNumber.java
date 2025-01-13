package faang.school.accountservice.controller;


import faang.school.accountservice.dto.Accoun.RequestAccount;
import faang.school.accountservice.service.FreeAccountNumberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Validated
@RequiredArgsConstructor
public class CreateFreeAccountNumber {
    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping
    public void createFreeAccountNumber(@RequestBody @Valid RequestAccount requestAccount) {
        freeAccountNumberService.createFreeNumber(requestAccount);
    }
}
