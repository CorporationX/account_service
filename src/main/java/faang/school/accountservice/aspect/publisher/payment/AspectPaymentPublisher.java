package faang.school.accountservice.aspect.publisher.payment;

import faang.school.accountservice.annotation.publisher.PublishPayment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@RequiredArgsConstructor
@Component
public class AspectPaymentPublisher {
    private final List<PaymentPublisher<?>> publishers;
    private final Map<Class<?>, PaymentPublisher<?>> publishersMap = new HashMap<>();

    @PostConstruct
    public void mapInit() {
        publishers.forEach(publisher -> publishersMap.put(publisher.getInstance(), publisher));
    }

    @AfterReturning(pointcut = "@annotation(publishPayment)", returning = "returnedValue",
            argNames = "publishPayment,returnedValue")
    public void execute(PublishPayment publishPayment, Object returnedValue) {
        PaymentPublisher<?> publisher = publishersMap.get(publishPayment.returnedType());

        publisher.makeResponse(returnedValue);
        publisher.publish();
    }

    @AfterThrowing(pointcut = "@annotation(publishPayment)", throwing = "exception",
            argNames = "joinPoint, publishPayment, exception")
    public void handle(JoinPoint joinPoint, PublishPayment publishPayment, Exception exception) {
        PaymentPublisher<?> publisher = publishersMap.get(publishPayment.returnedType());

        publisher.makeErrorResponse(joinPoint.getArgs()[0], exception);
        publisher.publish();
    }
}
