package faang.school.accountservice.client.project;

import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.utilities.UrlUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectFeignClient {
    @GetMapping(UrlUtils.PROJECT_MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS + UrlUtils.ID)
    ProjectDto getProject(@PathVariable("id") Long projectId);
}
