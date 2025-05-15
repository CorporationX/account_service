package faang.school.accountservice.messages;

public class ErrorMessages {
    //Account
    public static final String ACCOUNT_NOT_FOUND = "Account with number %s not found";

    //Request
    public static final String USER_HAS_ALREADY_OPENED_REQUEST = "User with ID %d has already opened request";
    public static final String REQUEST_NOT_FOUND = "Request with idempotency token %s not found";
    public static final String ERROR_REQUEST_TASK  = "Error while executing request task: %s";
    public static final String REQUEST_TASK_EXECUTION_FAILED  = "Task execution failed\n";
    //Handler
    public static final String HANDLER_NOT_FOUND  = "Handler not found: %s";

    public static final String INIT_ERROR = "Initialization of RequestSchedulerService failed";
    public static final String EXECUTOR_NOT_FOUND = "No executor found for request type: ";
    public static final String EXECUTION_FAILED = "Failed to execute request %s. Error: %s";
    public static final String PROCESS_ERROR = "Error while processing scheduled requests";
    public static final String SHUTDOWN_ERROR = "Error during shutdown of RequestSchedulerService";
    public static final String EXECUTOR_SHUTDOWN_INTERRUPTED = "Executor shutdown interrupted";

    //Object mapper
    public static final String FAILED_TO_CONVERT_MAP_TO_JSON = "Failed to convert map to JSON";
    public static final String FAILED_TO_CONVERT_JSON_TO_MAP = "Failed to convert JSON to map";
    public static final String SERIALIZATION_ERROR = "Serialization error";
}
