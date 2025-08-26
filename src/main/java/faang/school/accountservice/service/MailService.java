package faang.school.accountservice.service;

import faang.school.accountservice.enums.RequestStatus;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    public void sendNotification(String email, RequestStatus status) {
        // Логика отправки email
        String subject = "Статус вашего запроса";
        String body = "Ваш запрос имеет статус: " + status;

        // Отправка письма
        //emailSender.send(email, subject, body);
    }
}
