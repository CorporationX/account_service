package faang.school.accountservice.messages;

public class ErrorMessages {
    //Account
    public static final String ACCOUNT_NOT_FOUND = "Account with number %s not found";

    //Request
    public static final String USER_HAS_ALREADY_OPENED_REQUEST = "User with ID %d has already opened request";
    public static final String REQUEST_NOT_FOUND = "Request with idempotency token %s not found";

    //Object mapper
    public static final String FAILED_TO_CONVERT_MAP_TO_JSON = "Failed to convert map to JSON";
    public static final String FAILED_TO_CONVERT_JSON_TO_MAP = "Failed to convert JSON to map";
    public static final String SERIALIZATION_ERROR = "Serialization error";
}
