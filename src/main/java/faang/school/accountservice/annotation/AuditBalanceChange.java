package faang.school.accountservice.annotation;

import faang.school.accountservice.enums.AuditEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditBalanceChange {

    AuditEventType eventType();
}
