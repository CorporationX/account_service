package faang.school.accountservice.client;

import faang.school.accountservice.dto.project.ProjectDto;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${project-service.url}")
public interface ProjectServiceClient {

    @GetMapping("/projects/{projectId}")
    ProjectDto getProjectById(@PathVariable @Positive Long projectId);
}
