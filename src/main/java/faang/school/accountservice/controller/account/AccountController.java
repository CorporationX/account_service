package faang.school.accountservice.controller.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping("/{id}/balance")
    public ResponseEntity<Long> getAccountBalance(@PathVariable Long id) {
        return ResponseEntity.ok(1000L);
    }
}
