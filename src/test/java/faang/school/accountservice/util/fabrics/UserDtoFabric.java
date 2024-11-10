package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.dto.user.UserDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDtoFabric {
    public static UserDto buildUserDto() {
        return UserDto.builder()
                .build();
    }

    public static UserDto buildUserDtoDefault(Long id) {
        return UserDto.builder()
                .id(id)
                .username("Test name")
                .email("test@mail.com")
                .phone("1234567890")
                .active(true)
                .aboutMe("Nothing")
                .preference("Nothing")
                .build();
    }
}
