package kz.medet.userservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class HomeControllerAspects {

    @Before("execution(* kz.medet.userservice.controller.HomeController.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Method invoked: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* kz.medet.userservice.controller.HomeController.*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {

        log.info("Method completed: {} with return value: {}", joinPoint.getSignature().toShortString(), result);
    }
}