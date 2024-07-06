package org.myworkspace.UserServiceApplication.Aspects;

import jakarta.validation.ValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.lang.reflect.Field;

@Aspect
@Component
public class ValidEmailAspect {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    @Around("execution(* *(..)) && @annotation@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object validateEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Field[] fields = signature.getMethod().getDeclaringClass().getDeclaredFields();

        for (Object arg : args) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ValidEmail.class)) {
                    field.setAccessible(true);
                    String email = (String) field.get(arg);
                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        ValidEmail annotation = field.getAnnotation(ValidEmail.class);
                        throw new ValidationException(annotation.message());
                    }
                }
            }
        }
        return joinPoint.proceed();
    }
}
