package faang.school.accountservice.service;

import faang.school.accountservice.config.redis.AnswerSendingTest;
import faang.school.accountservice.dto.AnswerDto;
import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DmsService {
    private final AnswerSendingTest answerSendingTest;

    public void sendTest(Long id) {
        AnswerDto answerDto = AnswerDto.builder()
                .id(id)
                .status(PaymentStatus.AUTHORIZED)
                .build();

        answerSendingTest.answer(answerDto);
        log.info("Отправили сообщение из ДМС СЕРВИСА: " + answerDto);
    }
}