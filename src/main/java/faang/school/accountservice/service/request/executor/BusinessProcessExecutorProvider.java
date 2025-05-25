package faang.school.accountservice.service.request.executor;

import java.util.concurrent.ThreadPoolExecutor;

public interface BusinessProcessExecutorProvider {
    String getSupportedType();

    ThreadPoolExecutor getExecutor();
}
