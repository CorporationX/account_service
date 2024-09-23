package faang.school.accountservice.controller;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/request/")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public void createRequest(@RequestBody RequestDto requestDto){
        requestService.createRequest(requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllRequests(@RequestParam Long userId){
        return requestService.getAllRequests(userId);
    }

}
