package kz.medet.userservice.aop;

import org.aspectj.lang.annotation.Pointcut;

public class MyPointCuts {

    @Pointcut("execution(* kz.medet.userservice.service.impl.CustomerService.getAllCustomers())")
    public void getCustomersMethod(){}

    @Pointcut("execution(void kz.medet.userservice.service.impl.CustomerService.addCustomer(..))")
    public void addCustomerMethod(){}

    @Pointcut("execution(void kz.medet.userservice.service.impl.CustomerService.addOrderToCustomer(..))")
    public void addOrderToCustomerMethod(){}

    @Pointcut("execution(* kz.medet.userservice.service.impl.CustomerService.getCustomerOrders(..))")
    public void getCustomerOrdersMethod(){}

    @Pointcut("execution(* kz.medet.userservice.service.impl.CustomerService.addProductToOrder(..))")
    public void addProductToOrderMethod(){}
}
