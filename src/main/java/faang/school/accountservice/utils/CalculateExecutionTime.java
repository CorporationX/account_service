package faang.school.accountservice.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CalculateExecutionTime {

    String name() default "";
    TimeUnit timeUnit() default TimeUnit.MILLI_SECONDS;

    enum TimeUnit{
        NANO_SECONDS, MILLI_SECONDS, SECONDS, MINUTES, HOURS, DAYS
    }
}
