package kz.medet.userservice.clients;

import kz.medet.userservice.dto.OrderDto;
import kz.medet.userservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @PostMapping("/orders/{customerId}")
    void createOrder(@PathVariable Long customerId);

    @GetMapping("/orders/customer/{customerId}")
    OrderDto getOrderByCustomerId(@PathVariable Long customerId);

    @PostMapping("/orders/{orderId}/addProduct")
    void addProductToOrder(@PathVariable Long orderId,
                           @RequestParam String productName,
                           @RequestParam double productPrice);

    @GetMapping("/orders/{orderId}/products")
    List<ProductDto> getProductsByOrderId(@PathVariable Long orderId);
}
