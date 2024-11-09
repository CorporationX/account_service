package faang.school.accountservice.controller;

import faang.school.accountservice.model.Reserve;
import faang.school.accountservice.service.ReserveService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveService reserveService;

    @GetMapping("/{id}")
    public Reserve getReserve(@Positive @PathVariable("id") long id) {
        return reserveService.getReserve(id);
    }
}
