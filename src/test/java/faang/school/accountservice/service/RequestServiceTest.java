package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.ResponseRequestDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private RequestMapperImpl mapper;

    @InjectMocks
    private RequestService requestService;

    @Test
    void getRequestByIdempotentTokenTestFoundRequest() {
        String token = "123qwe";
        Long userId = 1L;
        Request request = createRequestWithTokenAndUserId(userId, token);

        when(requestRepository.findByIdempotentTokenForUpdate(token)).thenReturn(Optional.ofNullable(request));

        Request result = requestService.getRequestByIdempotentToken(token);

        assertEquals(request, result);
    }

    @Test
    void getRequestByIdempotentTokenTestNotFoundRequest() {
        String token = "123qwe";

        Request result = requestService.getRequestByIdempotentToken(token);

        assertNull(result);
    }

    @Test
    void createRequestTest() {
        String token = "123qwe";
        Long userId = 1L;

        CreateRequestDto createRequestDto = createCreateRequestDtoWithUserIDAndToken(userId, token);
        UserDto userDto = createUserDto(userId, "q", "a");

        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        ResponseRequestDto result = requestService.createRequest(createRequestDto);

        verify(requestRepository, times(1)).save(any());
        assertEquals(userId, result.userId());
    }


    @Test
    void createRequestTestOccupiedToken() {
        String token = "123qwe";
        Long userId = 1L;
        Request request = createRequestWithTokenAndUserId(userId, token);
        CreateRequestDto createRequestDto = createCreateRequestDtoWithUserIDAndToken(userId, token);
        UserDto userDto = createUserDto(userId, "q1", "a1");

        when(requestRepository.findByIdempotentTokenForUpdate(token)).thenReturn(Optional.ofNullable(request));
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(createRequestDto));
    }

    @Test
    void openRequestTest() {
        String token = "qaz1";
        Long userId = 1L;
        Request request = createRequestWithTokenAndUserId(userId, token);

        requestService.openRequest(request);

        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void closeRequestTest() {
        String token = "qaz1";
        Long userId = 1L;
        Request request = createRequestWithTokenAndUserId(userId, token);

        requestService.closeRequest(request);

        verify(requestRepository, times(1)).save(request);
    }

    private Request createRequestWithTokenAndUserId(Long userId, String token) {
        return Request.builder()
                .userId(userId)
                .idempotentToken(token)
                .build();
    }

    private CreateRequestDto createCreateRequestDtoWithUserIDAndToken(Long userId, String token) {
        return CreateRequestDto.builder()
                .userId(userId)
                .idempotentToken(token)
                .build();
    }

    private UserDto createUserDto(Long id, String username, String email) {
        return new UserDto(id, username, email);
    }
}