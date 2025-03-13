package kz.medet.userservice.controller;

import kz.medet.userservice.clients.OrderServiceClient;
import kz.medet.userservice.dto.OrderDto;
import kz.medet.userservice.dto.ProductDto;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.payload.response.MessageResponse;
import kz.medet.userservice.service.impl.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class HomeController {

    private final CustomerService customerService;


    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
    }

    @PostMapping("/addCustomer")
    public ResponseEntity<MessageResponse> createCustomer(@RequestParam String firstName,
                                                          @RequestParam String lastName) {
        customerService.addCustomer(firstName, lastName);

        return new ResponseEntity<>(new MessageResponse("Customer created"), HttpStatus.CREATED);
    }

    @PostMapping("/addOrderToCustomer/{customerId}")
    public ResponseEntity<MessageResponse> addOrderToCustomer(@PathVariable Long customerId) {
        customerService.addOrderToCustomer(customerId);


        return new ResponseEntity<>(new MessageResponse("Order created for Customer" + customerId), HttpStatus.CREATED);
    }

    @GetMapping("/showOrderOfCustomer/{customerId}")
    public ResponseEntity<OrderDto> getOrderOfCustomer(@PathVariable Long customerId) {
        return new ResponseEntity<>(customerService.getOrderOfCustomer(customerId), HttpStatus.OK);
    }

    @PostMapping("/addProductToOrder/{orderId}")
    public ResponseEntity<MessageResponse> addProductToOrder(@PathVariable Long orderId,
                                                             @RequestParam String productName,
                                                             @RequestParam double productPrice) {
        customerService.addProductToOrder(orderId, productName, productPrice);

        return new ResponseEntity<>(new MessageResponse("Product added to Order " + orderId), HttpStatus.CREATED);
    }

    @GetMapping("/showProductsOfOrder/{orderId}")
    public ResponseEntity<List<ProductDto>> getProductsOfOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(customerService.getProductsOfOrder(orderId));
    }


}