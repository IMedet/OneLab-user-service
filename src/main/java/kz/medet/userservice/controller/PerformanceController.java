package kz.medet.userservice.controller;

import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.service.impl.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

    private final CustomerService customerService;

    public PerformanceController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/compare-streams")
    public Map<String, Long> compareStreams() {
        return customerService.compareStreamPerformance();
    }

    @GetMapping("/group-by-lastname")
    public Map<String, List<Customer>> groupByLastName() {
        return customerService.groupCustomersByLastName();
    }
}

