package faang.school.accountservice.client;

import faang.school.accountservice.dto.achievement.AchievementDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "achievement-service", url = "${achievement-service.host}:${achievement-service.port}")
public interface AchievementServiceClient {

    @GetMapping("/api/v1/achievements")
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    List<AchievementDto> getByUserId(@RequestParam("user") Long userId);
}
