package faang.school.accountservice.controller;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestGetDto;
import faang.school.accountservice.service.request.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/requests")
@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestGetDto request(@Valid @RequestBody RequestCreateDto dto) {
        return requestService.createRequest(dto);
    }
}
