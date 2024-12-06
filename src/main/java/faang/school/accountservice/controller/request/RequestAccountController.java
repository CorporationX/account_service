package faang.school.accountservice.controller.request;

import faang.school.accountservice.dto.account.RequestDto;
import faang.school.accountservice.service.request.RequestExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/request")
@RequiredArgsConstructor
public class RequestAccountController {

    private final RequestExecutorService requestExecutorService;

    @PostMapping
    public void openingAccount(@RequestBody RequestDto requestDto) {
        requestExecutorService.openingAccount(requestDto);
    }
}
