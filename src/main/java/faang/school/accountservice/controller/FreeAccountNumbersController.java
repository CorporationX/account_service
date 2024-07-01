package faang.school.accountservice.controller;

import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/numbers")
public class FreeAccountNumbersController {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @PostMapping("/generation")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateAccountNumbersUpTo(@RequestBody FreeAccountNumberDto freeAccountNumberDto) {
        freeAccountNumbersService.generateAccountNumbersUpTo(freeAccountNumberDto.getNumber(), freeAccountNumberDto.getType());
    }
}
