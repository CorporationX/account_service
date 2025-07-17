package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savingAccount.ResponseSavingDto;
import faang.school.accountservice.dto.savingAccount.SavingCreateDto;
import faang.school.accountservice.facade.SavingAccountFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/saving")
@RequiredArgsConstructor
public class SavingAccountController {

    private final SavingAccountFacade savingAccountFacade;

    @PostMapping("/new")
    public ResponseEntity<ResponseSavingDto> createSavingAccount(@RequestBody SavingCreateDto requestDto) {
        ResponseSavingDto responseDto = savingAccountFacade.createdSavingsAccount(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("saving")
    public ResponseEntity<ResponseSavingDto> getSavingAccount() {
        ResponseSavingDto responseDto = savingAccountFacade.createdSavingsAccount();
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("saving")
    public ResponseEntity<ResponseSavingDto> updateTariffSavingAccount() {
        ResponseSavingDto responseDto = savingAccountFacade.updateTariffSavingAccount();
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping
    public ResponseEntity<ResponseSavingDto> deleteSavingAccount() {
        ResponseSavingDto responseDto = savingAccountFacade.deleteSavingAccount();
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("saving")
    public ResponseEntity<ResponseSavingDto> depositToAccount() {
        ResponseSavingDto responseDto = savingAccountFacade.depositToAccount();
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("saving")
    public ResponseEntity<ResponseSavingDto> withdraw() {
        ResponseSavingDto responseDto = savingAccountFacade.withdraw();
        return ResponseEntity.ok(responseDto);
    }
}
