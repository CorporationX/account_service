package faang.school.accountservice.service.bisnes_procces.impl;

import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.service.bisnes_procces.ProcessExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class CreateAccountProcess implements ProcessExecutor {

    @Qualifier("createAccountThreadPool")
    private final Executor createAccountExecutor;

    public CreateAccountProcess(@Qualifier("createAccountThreadPool") Executor createAccountExecutor) {
        this.createAccountExecutor = createAccountExecutor;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_ACCOUNT;
    }

    @Override
    public Executor getThreadPoolExecutor() {
        return createAccountExecutor;
    }
}
