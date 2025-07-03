package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.ResponseRequestDto;
import faang.school.accountservice.service.request.RequestServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {

    private final RequestServiceImpl requestServiceImpl;

    @PostMapping
    public ResponseRequestDto createRequest(@Valid @RequestBody CreateRequestDto createRequestDto){
        return requestServiceImpl.createRequest(createRequestDto);
    }
}
