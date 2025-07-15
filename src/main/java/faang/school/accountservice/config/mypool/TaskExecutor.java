package faang.school.accountservice.config.mypool;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class TaskExecutor implements DisposableBean {

    private ExecutorService executor;

    @Bean(name = "eventSender")
    public ExecutorService taskExecutor() {
        this.executor = Executors.newFixedThreadPool(10);
        return this.executor;
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
}
