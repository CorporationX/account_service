package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> get(@PathVariable Long id){
        return ResponseEntity.ok(accountService.get(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> open(@RequestBody AccountRequestDto accountDto){
        return ResponseEntity.ok(accountService.open(accountDto));
    }

    @PutMapping("/{id}/block")
    public void block(@PathVariable Long id){
        accountService.block(id);
    }

    @PutMapping("/{id}/close")
    public void close(@PathVariable Long id){
        accountService.close(id);
    }
}
