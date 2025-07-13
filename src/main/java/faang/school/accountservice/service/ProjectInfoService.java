package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.dto.client.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectInfoService {

    private final ProjectServiceClient projectServiceClient;

    @Retryable(retryFor = Exception.class)
    public ProjectDto getProjectInfoById(Long projectId) {
        return projectServiceClient.getProjectById(projectId);
    }
}