package kz.medet.userservice.service.impl;

import kz.medet.userservice.dto.CreateProductDto;
import kz.medet.userservice.dto.OrderResponse;
import kz.medet.userservice.dto.Product;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import kz.medet.userservice.exceptions.CustomException;
import kz.medet.userservice.kafka.KafkaConsumer;
import kz.medet.userservice.kafka.KafkaProducer;
import kz.medet.userservice.repository.CustomerRepository;
import kz.medet.userservice.repository.CustomerSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaProducer kafkaProducer;

    private final KafkaConsumer kafkaConsumer;
    private final BlockingQueue blockingQueue;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerService(CustomerRepository customerRepository,
                           KafkaProducer kafkaProducer,
                           KafkaConsumer kafkaConsumer,
                           BlockingQueue blockingQueue,
                           CustomerSearchRepository customerSearchRepository) {
        this.customerRepository = customerRepository;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.blockingQueue = blockingQueue;
        this.customerSearchRepository=customerSearchRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomer(String firstName, String lastName) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        Customer savedCustomer = customerRepository.save(customer);

        CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.setId(savedCustomer.getId().toString());
        customerDocument.setFirstName(savedCustomer.getFirstName());
        customerDocument.setLastName(savedCustomer.getLastName());
        customerDocument.setOrderId(savedCustomer.getOrderId());

        customerSearchRepository.save(customerDocument);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addOrderToCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomException("The customer doesn't exist"));

        kafkaProducer.sendMessage(customerId);
        String response;
        try {
            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
            if (response==null){
                throw new CustomException("Timeout: No response from OrderService");
            }
        }catch (InterruptedException e){
            throw new CustomException(e.getMessage());
        }
        customer.setOrderId(Long.parseLong(response));
        customerRepository.save(customer);

        customerSearchRepository.deleteById(customerId.toString());
        CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.setId(customer.getId().toString());
        customerDocument.setFirstName(customer.getFirstName());
        customerDocument.setLastName(customer.getLastName());
        customerDocument.setOrderId(customer.getOrderId());

        customerSearchRepository.save(customerDocument);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderResponse getCustomerOrders(Long customerId){
        kafkaProducer.sendMessage2(customerId);
        String response;
        try {
            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
            if (response==null){
                throw new CustomException("Timeout: No response from OrderService");
            }
        }catch (InterruptedException e){
            throw new CustomException(e.getMessage());
        }
        return kafkaConsumer.getOrderResponse(response);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String addProductToOrder(Long orderId, String name, double price) {
        kafkaProducer.sendMessage3(new CreateProductDto(orderId,name,price));
        String response;
        try {
            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
            if (response==null){
                throw new CustomException("Timeout: No response from OrderService");
            }
        }catch (InterruptedException e){
            throw new CustomException(e.getMessage());
        }
        return response;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<CustomerDocument> searchCustomers(String query) {
        return customerSearchRepository.findByFirstNameContainingOrLastNameContaining(query, query);
    }

//    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
//    public List<Product> getAllProductsOfOrder(Long orderId) {
//        OrderDto orderDto = orderRepository.findById(orderId).orElseThrow(
//                () -> new ResourceNotFoundException("Order", "OrderId", orderId));
//        return orderDto.getProducts();
//    }

}
