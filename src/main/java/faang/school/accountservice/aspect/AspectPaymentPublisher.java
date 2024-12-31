package faang.school.accountservice.aspect;

import faang.school.accountservice.annotation.PublishPayment;
import faang.school.accountservice.publisher.payment.abstracts.PaymentPublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class AspectPaymentPublisher {

    private final List<PaymentPublisher<?>> publishers;
    private final Map<Class<?>, PaymentPublisher<?>> publishersMap = new HashMap<>();

    @PostConstruct
    public void initPublishersMap() {
        for (PaymentPublisher<?> publisher : publishers) {
            publishersMap.put(publisher.getInstance(), publisher);
            log.debug("Registered publisher for type: {}", publisher.getInstance().getSimpleName());
        }
    }

    @AfterReturning(pointcut = "@annotation(publishPayment)", returning = "returnedValue",
            argNames = "publishPayment,returnedValue")
    public void afterReturningPublishEvent(PublishPayment publishPayment, Object returnedValue) {
        PaymentPublisher<?> publisher = publishersMap.get(publishPayment.returnedType());
        if (publisher == null) {
            log.warn("No publisher found for type: {}", publishPayment.returnedType().getSimpleName());
            return;
        }
        publisher.makeResponse(returnedValue);
        publisher.publish();
    }

    @AfterThrowing(pointcut = "@annotation(publishPayment)", throwing = "exception",
            argNames = "joinPoint,publishPayment,exception")
    public void afterThrowingPublishEvent(JoinPoint joinPoint, PublishPayment publishPayment, Exception exception) {
        PaymentPublisher<?> publisher = publishersMap.get(publishPayment.returnedType());
        if (publisher == null) {
            log.warn("No publisher found for type: {}", publishPayment.returnedType().getSimpleName());
            return;
        }
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            log.warn("No args found for method with @PublishPayment");
            return;
        }
        publisher.makeErrorResponse(args[0], exception);
        publisher.publish();
    }
}