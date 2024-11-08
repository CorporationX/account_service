package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private RequestMapperImpl requestMapper;

    @Mock
    private RequestStatusPublisher requestStatusPublisher;

    @InjectMocks
    private RequestServiceImpl requestService;

    private UUID requestId;
    private RequestDto updateDto;
    private RequestDto event;
    private Request correctRequest;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        updateDto = RequestDto.builder()
                .id(requestId)
                .status(RequestStatus.OPEN)
                .build();
        event = new RequestDto();
        correctRequest = new Request();
    }

    @Test
    void createRequest_shouldSaveRequestAndPublishEvent() {
        RequestDto requestDto = new RequestDto();
        when(requestRepository.save(correctRequest)).thenReturn(correctRequest);

        RequestDto result = requestService.createRequest(requestDto);

        assertEquals(requestDto, result);
        verify(requestRepository).save(correctRequest);
        verify(requestStatusPublisher).publish(any(RequestDto.class));
        verify(requestMapper).toEntity(requestDto);
        verify(requestMapper).toDto(correctRequest);
        verify(requestMapper).toEvent(requestDto);
    }


    @Test
    void updateRequest_shouldUpdateRequestAndPublishEvent() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(correctRequest));
        when(requestMapper.toDto(correctRequest)).thenReturn(updateDto);
        when(requestRepository.save(correctRequest)).thenReturn(correctRequest);
        when(requestMapper.toEvent(updateDto)).thenReturn(event);

        RequestDto result = requestService.updateRequest(updateDto);

        assertEquals(updateDto, result);
        verify(requestMapper).updateEntityFromDto(updateDto, correctRequest);
        verify(requestRepository).save(correctRequest);
        verify(requestStatusPublisher).publish(event);
    }

    @Test
    void updateRequest_shouldThrowExceptionWhenIdIsNull() {
        String correctMessage = "Id is null";
        updateDto.setId(null);

        Exception exception = assertThrows(DataValidationException.class,
                () -> requestService.updateRequest(updateDto));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void updateRequest_shouldThrowExceptionWhenRequestNotFound() {
        String correctMessage = "Request with id %s not found".formatted(requestId);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.updateRequest(updateDto));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void updateRequest_shouldNotPublishEventWhenStatusIsNull() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(correctRequest));
        when(requestMapper.toDto(correctRequest)).thenReturn(updateDto);
        when(requestRepository.save(correctRequest)).thenReturn(correctRequest);

        RequestDto result = requestService.updateRequest(updateDto);

        assertEquals(updateDto, result);
        verify(requestStatusPublisher, never()).publish(event);
    }
}
