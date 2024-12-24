package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.tariff.TariffChangeHistoryDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.model.tariff.TariffChangeRecord;
import faang.school.accountservice.service.tariff.TariffService;
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
@RequestMapping("api/v1/tariffs")
public class TariffController {

    private final TariffService tariffService;
    private final UserValidator userValidator;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffResponseDto create(@Valid @RequestBody TariffRequestDto tariffRequestDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userContext.getUserId());
        log.info("Received request to create a new tariff of type {} by user with id: {}",
                tariffRequestDto.getTariffType(), userId);
        return tariffService.create(tariffRequestDto, userId);
    }

    @PutMapping("/{tariffId}")
    public TariffResponseDto update(@PathVariable("tariffId") Long tariffId,
                                    @Valid @RequestBody TariffRequestDto tariffRequestDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        log.info("Received a request to update a tariff with id {} by user with id {}",
                tariffId, userId);
        return tariffService.update(tariffId, tariffRequestDto, userId);
    }

    @GetMapping("/{tariffId}")
    public TariffResponseDto get(@PathVariable("tariffId") Long tariffId) {
        log.info("Received a request to get a tariff with id {}", tariffId);
        return tariffService.get(tariffId);
    }

    @GetMapping()
    public List<TariffResponseDto> getAll() {
        log.info("Received a request to get all tariffs");
        return tariffService.getAll();
    }

    @DeleteMapping("/{tariffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("tariffId") Long tariffId) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        log.info("Received a request to delete a tariff with id {} by user with id {}",
                tariffId, userId);
        tariffService.delete(tariffId);
    }

    @GetMapping("/{tariffId}/history")
    public TariffChangeHistoryDto getHistory(@PathVariable("tariffId") Long tariffId) {
        List<TariffChangeRecord> changes = tariffService.getTariffChangeRecords(tariffId);
        return new TariffChangeHistoryDto(changes);
    }
}