package kz.medet.userservice.aop;

import kz.medet.userservice.dto.OrderResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("kz.medet.userservice.aop.MyPointCuts.addCustomerMethod()")
    public void beforeAddCustomerLoggingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("beforeAddCustomerLoggingAdvice: попытка добавления customer, метод: " + methodSignature.getName());

        Object[] args = joinPoint.getArgs();
        for (Object obj : args) {
            logger.info("Переданный параметр: " + obj);
        }
    }

    @After("kz.medet.userservice.aop.MyPointCuts.getCustomersMethod()")
    public void afterGetCustomersLoggingAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("afterGetCustomersLoggingAdvice: получен список всех customers, метод: " + methodSignature.getMethod());
    }

    @Around("kz.medet.userservice.aop.MyPointCuts.addOrderToCustomerMethod()")
    public void aroundAddOrderToCustomerLoggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("aroundAddOrderToCustomerLoggingAdvice: попытка добавления Order к Customer");
        try {
            proceedingJoinPoint.proceed();
        } catch (Exception e) {
            logger.warn("Ошибка при добавлении Order к Customer: " + e.getMessage());
        }
    }

    @AfterReturning(value = "kz.medet.userservice.aop.MyPointCuts.getCustomerOrdersMethod()", returning = "orderResponse")
    public void afterReturningGetCustomerOrders(OrderResponse orderResponse) {
        logger.info("afterReturningGetCustomerOrders: заказ получен: " + orderResponse);
    }

    @AfterThrowing(value = "kz.medet.userservice.aop.MyPointCuts.addProductToOrderMethod()", throwing = "exception")
    public void afterThrowingAddProductToOrder(Throwable exception) {
        logger.warn("afterThrowingAddProductToOrder: ошибка при добавлении продукта к заказу: " + exception.getMessage());
    }
}

