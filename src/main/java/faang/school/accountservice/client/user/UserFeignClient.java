package faang.school.accountservice.client.user;

import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.utilities.UrlUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserFeignClient {
    @GetMapping(UrlUtils.USER_MAIN_URL + UrlUtils.V1 + UrlUtils.USERS + UrlUtils.ID)
    UserDto getUser(@PathVariable("id") long userId);
}
