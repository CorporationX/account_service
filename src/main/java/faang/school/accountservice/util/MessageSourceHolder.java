package faang.school.accountservice.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceHolder implements InitializingBean {

    private final MessageSource messageSource;

    public static MessageSource STATIC_MESSAGE_SOURCE;

    public MessageSourceHolder(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void afterPropertiesSet() {
        STATIC_MESSAGE_SOURCE = this.messageSource;
    }
}