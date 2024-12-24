package faang.school.accountservice.controller.interest_rate;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.interest_rate.InterestRateChangeHistoryDto;
import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.model.interest_rate.InterestRateChangeRecord;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import faang.school.accountservice.validator.interest_rate.InterestRateValidator;
import faang.school.accountservice.validator.user.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/interest_rates")
public class InterestRateController {

    private final UserValidator userValidator;
    private final InterestRateValidator interestRateValidator;
    private final InterestRateService interestRateService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestRateDto create(@Valid @RequestBody InterestRateDto interestRateDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        interestRateValidator.validateInterestRateDoesNotExceedMax(interestRateDto);
        log.info("Received request to create a new interest rate {} by user with id {}",
                interestRateDto.getInterestRate(), userId);
        return interestRateService.create(interestRateDto, userId);
    }

    @PutMapping("/{interestRateId}")
    public InterestRateDto update(@PathVariable("interestRateId") Long interestRateId,
                                  @Valid @RequestBody InterestRateDto interestRateDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        interestRateValidator.validateInterestRateDoesNotExceedMax(interestRateDto);
        log.info("Received a request to update an interestRate with id {} to {} by user with id {}",
                interestRateId, interestRateDto.getInterestRate(), userId);
        return interestRateService.update(interestRateId, interestRateDto, userId);
    }

    @GetMapping("/{interestRateId}")
    public InterestRateDto get(@PathVariable("interestRateId") Long interestRateId) {
        log.info("Received a request to get an interestRate with id {}", interestRateId);
        return interestRateService.get(interestRateId);
    }

    @GetMapping()
    public List<InterestRateDto> getAll() {
        log.info("Received a request to get all interest rates");
        return interestRateService.getAll();
    }

    @DeleteMapping("/{interestRateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("interestRateId") Long interestRateId) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        log.info("Received a request to delete an interestRate with id {} by user with id {} ",
                interestRateId, userId);
        interestRateService.delete(interestRateId);
    }

    @GetMapping("/{interestRateId}/history")
    public InterestRateChangeHistoryDto getHistory(@PathVariable("interestRateId") Long interestRateId) {
        List<InterestRateChangeRecord> changes = interestRateService.getInterestRateChangeRecords(interestRateId);
        return new InterestRateChangeHistoryDto(changes);
    }
}