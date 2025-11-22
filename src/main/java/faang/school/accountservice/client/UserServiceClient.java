package faang.school.accountservice.client;

import faang.school.accountservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",
        url = "${user-service.host}:${user-service.port}",
        path = "${user-service.context-path}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId);
}