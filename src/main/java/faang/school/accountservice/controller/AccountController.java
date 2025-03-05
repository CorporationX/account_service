package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@Slf4j
public class AccountController {
    private final AccountService accountService;

//    get c фильтрами по периоду статусу владельцу и типу владельца
    // делаем через response и делаем норм запросы сразу к БД

    @PostMapping("/open")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(accountService.createAccount(accountDto));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<Account> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Account> closeAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }
}
//Exception Handler Specification
//После выполнения основной логики можно идти дальше: подумать, какие
// методы необходимо реализовать для работы со счетами? Какие ограничения накладываются на счета,
// какие интересные особенности платежных сервисов здесь нужно учесть? Все эти действия реализовать как методы в сервисном слое
//
