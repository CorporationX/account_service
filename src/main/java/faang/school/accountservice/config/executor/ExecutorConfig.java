package faang.school.accountservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final ExecutorProperties executorProperties;
    // привинчиваю бины экзекьюторов и делаю их тиканье через хэндлеры
}
