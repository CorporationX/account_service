package faang.school.accountservice.service.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncRequestProcessor {

    private static final long TRANSACTION_COMMIT_DELAY_MS = 200;

    private final RequestProcessor requestProcessor;

    @Async
    public void processAsync(UUID idempotencyToken) {
        try {
            TimeUnit.MILLISECONDS.sleep(TRANSACTION_COMMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        log.info("Starting async processing for request: {}", idempotencyToken);
        requestProcessor.process(idempotencyToken);
    }
}