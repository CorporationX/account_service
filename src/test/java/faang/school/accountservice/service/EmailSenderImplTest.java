package faang.school.accountservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderImpl emailSender;

    @Test
    void testSendEmail_Success() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        emailSender.sendEmail(to, subject, content);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
