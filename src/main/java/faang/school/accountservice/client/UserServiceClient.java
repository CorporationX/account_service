package faang.school.accountservice.client;

import faang.school.accountservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);
}
