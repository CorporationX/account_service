package faang.school.accountservice.controller;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.service.RequestExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/request")
@RequiredArgsConstructor
public class AsyncRequestController {
    private final RequestExecutorService requestExecutorService;

    @GetMapping("/{id}")
    public RequestStatus getStatus(@PathVariable UUID id) {
        return requestExecutorService.getStatus(id);
    }
}
