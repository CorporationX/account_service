package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.dto.project.ProjectReadDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectServiceClient projectServiceClient;

    ProjectReadDto getProjectById(long id) {
        try {
            return projectServiceClient.getProject(id);
        } catch (FeignException.BadRequest e) {
            throw new EntityNotFoundException("Проект не найден");
        } catch (FeignException e) {
            throw new ExternalServiceException("Project service недоступен");
        }
    }
}
