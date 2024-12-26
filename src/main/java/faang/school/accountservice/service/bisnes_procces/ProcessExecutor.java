package faang.school.accountservice.service.bisnes_procces;

import faang.school.accountservice.enums.request.RequestType;

import java.util.concurrent.Executor;

public interface ProcessExecutor {

    RequestType getRequestType();

    Executor getThreadPoolExecutor();
}
