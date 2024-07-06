package org.myworkspace.UserServiceApplication.Aspects;

import jakarta.validation.ValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

@Aspect
@Component
public class ValidContactAspect {
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    @Around("execution(* *(..)) && @annotation@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object validateContact(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Field[] fields = signature.getMethod().getDeclaringClass().getDeclaredFields();

        for (Object arg : args) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ValidContact.class)) {
                    field.setAccessible(true);
                    String contact = (String) field.get(arg);
                    if (!CONTACT_PATTERN.matcher(contact).matches()) {
                        ValidContact annotation = field.getAnnotation(ValidContact.class);
                        throw new ValidationException(annotation.message());
                    }
                }
            }
        }
        return joinPoint.proceed();
    }
}
