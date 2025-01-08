package faang.school.accountservice.client;

import faang.school.accountservice.dto.AchievementDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "achievement-service", url = "${services.achievement-service.host}:${services.achievement-service.port}")
public interface AchievementServiceClient {

    @GetMapping("/api/v1/achievement/{achievementId}")
    AchievementDto getAchievement(@PathVariable long achievementId);
}