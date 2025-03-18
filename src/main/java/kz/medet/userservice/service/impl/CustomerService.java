package kz.medet.userservice.service.impl;

import kz.medet.userservice.clients.OrderServiceClient;
import kz.medet.userservice.dto.*;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import kz.medet.userservice.exceptions.CustomException;
import kz.medet.userservice.kafka.KafkaConsumer;
import kz.medet.userservice.kafka.KafkaProducer;
import kz.medet.userservice.mapper.CustomerMapper;
import kz.medet.userservice.repository.CustomerRepository;
import kz.medet.userservice.repository.CustomerSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;
    private final BlockingQueue blockingQueue;
    private final OrderServiceClient orderServiceClient;
    private final CustomerSearchRepository customerSearchRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository,
                           KafkaProducer kafkaProducer,
                           KafkaConsumer kafkaConsumer,
                           BlockingQueue blockingQueue,
                           CustomerSearchRepository customerSearchRepository,
                           OrderServiceClient orderServiceClient,
                           CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.blockingQueue = blockingQueue;
        this.customerSearchRepository = customerSearchRepository;
        this.orderServiceClient = orderServiceClient;
        this.customerMapper = customerMapper;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Map<String, Long> compareStreamPerformance() {
        List<Customer> customers = customerRepository.findAll();

        long startSequential = System.currentTimeMillis();
        customers.stream()
                .map(customerMapper::toResponseDto)
                .toList();
        long sequentialTime = System.currentTimeMillis() - startSequential;

        long startParallel = System.currentTimeMillis();
        customers.parallelStream()
                .map(customerMapper::toResponseDto)
                .toList();
        long parallelTime = System.currentTimeMillis() - startParallel;

        System.out.println("Время выполнения (последовательно): " + sequentialTime + " мс");
        System.out.println("Время выполнения (параллельно): " + parallelTime + " мс");

        Map<String, Long> result = new HashMap<>();
        result.put("sequential", sequentialTime);
        result.put("parallel", parallelTime);
        return result;
    }


    @Transactional(readOnly = true)
    public List<CustomerDocument> filterCustomers(String query, Long orderId) {
        return StreamSupport.stream(customerSearchRepository.findAll().spliterator(), false)
                .filter(c -> (query == null || c.getFirstName().toLowerCase().contains(query.toLowerCase())
                        || c.getLastName().toLowerCase().contains(query.toLowerCase())))
                .filter(c -> (orderId == null || c.getOrderId() != null && c.getOrderId().equals(orderId)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Map<String, List<Customer>> groupCustomersByLastName() {
        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .collect(Collectors.groupingBy(Customer::getLastName));
    }




    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public long countAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> 1L)
                .reduce(0L, Long::sum);
    }

    public long countCustomersWithOrders() {
        return getAllCustomers()
                .stream()
                .map(Customer::getOrderId)
                .filter(Objects::nonNull)
                .reduce(0L, (sum, orderId) -> sum + 1);
    }




    public List<CustomerResponseDto> getAllCustomer(String nameQuery, Long orderId, int page, int size) {
        if (nameQuery == null || nameQuery.isBlank()) {
            return getAllCustomers()
                    .parallelStream()
                    .filter(customer -> orderId == null || customer.getOrderId().equals(orderId))
                    .map(customerMapper::toResponseDto)
                    .toList();
        }

        List<CustomerDocument> customerDocuments = customerSearchRepository
                .findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(nameQuery, nameQuery);

        Set<Long> idsSet = customerDocuments.stream()
                .map(CustomerDocument::getId)
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        return customerRepository.findAllById(idsSet)
                .parallelStream()
                .map(customerMapper::toResponseDto)
                .filter(customer -> orderId == null || customer.getOrderId().equals(orderId))
                .toList();
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

//    @Transactional(propagation = Propagation.REQUIRED)
//    public void addOrderToCustomer(Long customerId) {
//        Customer customer = customerRepository.findById(customerId).orElseThrow(
//                () -> new CustomException("The customer doesn't exist"));
//
//        kafkaProducer.sendMessage(customerId);
//        String response;
//        try {
//            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
//            if (response==null){
//                throw new CustomException("Timeout: No response from OrderService");
//            }
//        }catch (InterruptedException e){
//            throw new CustomException(e.getMessage());
//        }
//        customer.setOrderId(Long.parseLong(response));
//        customerRepository.save(customer);
//
//        customerSearchRepository.deleteById(customerId.toString());
//        CustomerDocument customerDocument = new CustomerDocument();
//        customerDocument.setId(customer.getId().toString());
//        customerDocument.setFirstName(customer.getFirstName());
//        customerDocument.setLastName(customer.getLastName());
//        customerDocument.setOrderId(customer.getOrderId());
//
//        customerSearchRepository.save(customerDocument);
//    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addOrderToCustomer(Long customerId) {
        Optional<Customer> customer1 = customerRepository.findById(customerId);

        if (customer1.isPresent()) {
            Customer customer = customer1.get();
            kafkaProducer.sendMessage(customerId);

            orderServiceClient.createOrder(customerId);

            customer.setOrderId(customerId);
            customerRepository.save(customer);

            customerSearchRepository.deleteById(customerId.toString());
            CustomerDocument customerDocument = new CustomerDocument();
            customerDocument.setId(customer.getId().toString());
            customerDocument.setFirstName(customer.getFirstName());
            customerDocument.setLastName(customer.getLastName());
            customerDocument.setOrderId(customer.getOrderId());

            customerSearchRepository.save(customerDocument);
        }
    }

    @Transactional
    public OrderDto getOrderOfCustomer(Long customerId){
        return orderServiceClient.getOrderByCustomerId(customerId);
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public OrderResponse getCustomerOrders(Long customerId) {
//        kafkaProducer.sendMessage2(customerId);
//        String response;
//        try {
//            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
//            if (response == null) {
//                throw new CustomException("Timeout: No response from OrderService");
//            }
//        } catch (InterruptedException e) {
//            throw new CustomException(e.getMessage());
//        }
//        return kafkaConsumer.getOrderResponse(response);
//    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addProductToOrder(Long orderId, String product_name, double product_price) {
        orderServiceClient.addProductToOrder(orderId, product_name, product_price);
    }


    @Transactional
    public List<ProductDto> getProductsOfOrder(Long orderId) {
        return orderServiceClient.getProductsByOrderId(orderId);
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public String addProductToOrder(Long orderId, String name, double price) {
//        kafkaProducer.sendMessage3(new CreateProductDto(orderId,name,price));
//        String response;
//        try {
//            response = (String) blockingQueue.poll(15, TimeUnit.SECONDS);
//            if (response==null){
//                throw new CustomException("Timeout: No response from OrderService");
//            }
//        }catch (InterruptedException e){
//            throw new CustomException(e.getMessage());
//        }
//        return response;
//    }

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
