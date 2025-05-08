package faang.school.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CardTypeInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        freeAccountNumbersService.initCards();
    }
}
