package faang.school.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    OWNER_ID_NOT_PRESENT("Owner ID is not present", HttpStatus.BAD_REQUEST),
    MORE_ONE_OWNER("There may be only one owner", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
