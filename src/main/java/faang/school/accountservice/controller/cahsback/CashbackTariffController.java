package faang.school.accountservice.controller.cahsback;

import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cashback-tariffs")
@RequiredArgsConstructor
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;
}
