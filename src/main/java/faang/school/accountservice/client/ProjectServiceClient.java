package faang.school.accountservice.client;

import faang.school.accountservice.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${services.project-service.host}:${services.project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects/{projectId}")
    ProjectDto getProject(@PathVariable long projectId);
}
