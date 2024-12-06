package faang.school.accountservice.aspect;

import faang.school.accountservice.dto.account.RequestDto;
import faang.school.accountservice.service.request.RequestSchedulerService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccountDtoInterceptorAspect {

    private static final Logger logger = LoggerFactory.getLogger(AccountDtoInterceptorAspect.class);

    @Autowired
    private RequestSchedulerService requestSchedulerService;

    /**
     * Перехват вызова метода RequestController после его выполнения.
     **/
    @AfterReturning(
            pointcut = "execution(* faang.school.accountservice.controller.request.RequestAccountController.*(..)) " +
                    "&& args(requestDto)",
            returning = "result"
    )
    public void interceptRequestDto(RequestDto requestDto, Object result) {
        logger.info("Перехвачен аргумент метода: {}", requestDto);
        processAccountDto(requestDto);
    }

    private void processAccountDto(RequestDto requestDto) {
        if(requestDto == null || requestDto.getAccountCreateDto() == null) {
            logger.warn("Пустой RequestDto. Обработка пропущена.");
            return;
        }
        logger.info("Обработка requestDto: {}", requestDto);
        requestSchedulerService.addLastRequestDto(requestDto);
    }
}
