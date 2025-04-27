package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import faang.school.accountservice.exception_handler.GlobalExceptionHandler;
import faang.school.accountservice.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Faker faker = new Faker();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    public void testCreateRequest_Success() throws Exception {
        // Arrange
        var dto = getTestCreateRequestDto(); // заполните нужными данными

        // Act + Assert
        mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(requestService).createRequest(any(CreateRequestDto.class));
    }

    @Test
    public void testCreateRequest_ServiceUnavailableException() throws Exception {
        // Arrange
        var dto = getTestCreateRequestDto();
        doThrow(new ServiceUnavailableException("Service unavailable"))
                .when(requestService).createRequest(any());

        // Act + Assert
        mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code", is("SERVICE_UNAVAILABLE")))
                .andExpect(jsonPath("$.message", is("Service unavailable")));
    }

    @Test
    public void testCreateRequest_ResourceNotFoundException() throws Exception {
        // Arrange
        var dto = getTestCreateRequestDto();
        doThrow(new ResourceNotFoundException("Resource is not found"))
                .when(requestService).createRequest(any());

        // Act + Assert
        mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Resource is not found")));
    }

    @Test
    public void testUpdateRequestStatusByToken_Success() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        var status = RequestStatus.DONE;

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/status/{status}", token, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestService).updateRequestStatusByToken(token, status);
    }

    @Test
    public void testUpdateRequestStatusByToken_ResourceNotFoundException() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        var status = RequestStatus.DONE;

        doThrow(new ResourceNotFoundException("Request is not found"))
                .when(requestService).updateRequestStatusByToken(any(), any());

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/status/{status}", token, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Request is not found")));
    }

    @Test
    public void testUpdateRequestStatusByToken_DataValidationException() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        var status = RequestStatus.DONE;

        doThrow(new DataValidationException("Invalid status"))
                .when(requestService).updateRequestStatusByToken(any(), any());

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/status/{status}", token, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid status")));
    }

    @Test
    public void testUpdateRequestBodyByToken_Success() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        Map<String, Object> newBody = new HashMap<>();
        newBody.put("key", "value");

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/body", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBody)))
                .andExpect(status().isOk());
        verify(requestService).updateRequestBodyByToken(eq(token), anyMap());
    }

    @Test
    public void testUpdateRequestBodyByToken_ResourceNotFoundException() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        Map<String, Object> newBody = new HashMap<>();
        newBody.put("key", "value");

        doThrow(new ResourceNotFoundException("Request is not found"))
                .when(requestService).updateRequestBodyByToken(any(), anyMap());

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/body", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Request is not found")));
    }

    @Test
    public void testUpdateRequestBodyByToken_DataValidationException() throws Exception {
        // Arrange
        var token = UUID.randomUUID();
        Map<String, Object> newBody = new HashMap<>();
        newBody.put("key", "value");

        doThrow(new DataValidationException("Invalid data"))
                .when(requestService).updateRequestBodyByToken(any(), anyMap());

        // Act + Assert
        mockMvc.perform(patch("/api/v1/requests/{token}/body", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid data")));
    }

    private static CreateRequestDto getTestCreateRequestDto() {
        RequestType[] statuses = RequestType.values();
        var randomRequestType = statuses[faker.random().nextInt(statuses.length)];

        return CreateRequestDto.builder()
                .token(UUID.randomUUID())
                .userId(2L)
                .requestType(randomRequestType)
                .body(new HashMap<>())
                .build();
    }
}