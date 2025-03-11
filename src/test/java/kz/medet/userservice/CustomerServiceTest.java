package kz.medet.userservice;


import kz.medet.userservice.dto.CreateProductDto;
import kz.medet.userservice.dto.OrderResponse;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import kz.medet.userservice.exceptions.CustomException;
import kz.medet.userservice.kafka.KafkaConsumer;
import kz.medet.userservice.kafka.KafkaProducer;
import kz.medet.userservice.repository.CustomerRepository;
import kz.medet.userservice.repository.CustomerSearchRepository;
import kz.medet.userservice.service.impl.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerSearchRepository customerSearchRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaConsumer kafkaConsumer;

    private BlockingQueue<String> blockingQueue;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        blockingQueue = new ArrayBlockingQueue<>(1);
        customerService = new CustomerService(customerRepository, kafkaProducer, kafkaConsumer, blockingQueue, customerSearchRepository);
    }

    @Test
    void getAllCustomers_shouldReturnCustomers() {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "Almas", "Kairatov", null),
                new Customer(2L, "Petr", "Vlahov", null)
        );

        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void addCustomer_shouldSaveCustomerAndIndexInElasticsearch() {
        Customer customer = new Customer(1L, "Almas", "Kairatov", null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.addCustomer("Almas", "Kairatov");

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerSearchRepository, times(1)).save(any(CustomerDocument.class));
    }

    @Test
    void addOrderToCustomer_shouldUpdateOrderIdAndSaveToElasticsearch() throws InterruptedException {
        Customer customer = new Customer(1L, "Almas", "Kairatov", null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        blockingQueue.put("123");

        customerService.addOrderToCustomer(1L);

        assertEquals(123L, customer.getOrderId());
        verify(customerRepository, times(1)).save(customer);
        verify(customerSearchRepository, times(1)).deleteById("1");
        verify(customerSearchRepository, times(1)).save(any(CustomerDocument.class));
    }

    @Test
    void addOrderToCustomer_shouldThrowExceptionOnTimeout() {
        Customer customer = new Customer(1L, "Almas", "Kairatov", null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(CustomException.class, () -> customerService.addOrderToCustomer(1L));
    }

    @Test
    void getCustomerOrders_shouldReturnOrderResponse() throws InterruptedException {
        blockingQueue.put("orderResponse");
        OrderResponse mockResponse = new OrderResponse();
        when(kafkaConsumer.getOrderResponse("orderResponse")).thenReturn(mockResponse);

        OrderResponse result = customerService.getCustomerOrders(1L);

        assertNotNull(result);
        verify(kafkaProducer, times(1)).sendMessage2(1L);
    }

    @Test
    void getCustomerOrders_shouldThrowExceptionOnTimeout() {
        assertThrows(CustomException.class, () -> customerService.getCustomerOrders(1L));
    }

    @Test
    void addProductToOrder_shouldReturnResponse() throws InterruptedException {
        CreateProductDto productDto = new CreateProductDto(1L, "Product", 100.0);
        blockingQueue.put("ProductAdded");

        String result = customerService.addProductToOrder(1L, "Product", 100.0);

        assertEquals("ProductAdded", result);
        verify(kafkaProducer, times(1)).sendMessage3(productDto);
    }

    @Test
    void searchCustomers_shouldReturnMatchingCustomers() {
        List<CustomerDocument> searchResults = Arrays.asList(
                new CustomerDocument("1", "Almas", "Kairatov", 100L),
                new CustomerDocument("2", "Jane", "Smith", 200L)
        );

        when(customerSearchRepository.findByFirstNameContainingOrLastNameContaining("Almas", "Almas"))
                .thenReturn(searchResults);

        List<CustomerDocument> result = customerService.searchCustomers("Almas");

        assertEquals(2, result.size());
        verify(customerSearchRepository, times(1))
                .findByFirstNameContainingOrLastNameContaining("Almas", "Almas");
    }

    @Test
    void addProductToOrder_shouldThrowExceptionOnTimeout() {
        CreateProductDto productDto = new CreateProductDto(1L, "Product", 100.0);

        assertThrows(CustomException.class, () -> customerService.addProductToOrder(1L, "Product", 100.0));

        verify(kafkaProducer, times(1)).sendMessage3(productDto);
        verifyNoInteractions(kafkaConsumer);
    }

    @Test
    void addProductToOrder_shouldThrowExceptionOnInterruptedException() {
        CreateProductDto productDto = new CreateProductDto(1L, "Product", 100.0);

        BlockingQueue<String> mockQueue = mock(BlockingQueue.class);
        customerService = new CustomerService(customerRepository, kafkaProducer, kafkaConsumer, mockQueue, customerSearchRepository);

        try {
            when(mockQueue.poll(15, TimeUnit.SECONDS)).thenThrow(new InterruptedException("Thread interrupted"));
        } catch (InterruptedException e) {
            fail("Mocking poll() should not fail");
        }

        CustomException exception = assertThrows(CustomException.class, () ->
                customerService.addProductToOrder(1L, "Product", 100.0)
        );

        assertEquals("Thread interrupted", exception.getMessage());
        verify(kafkaProducer, times(1)).sendMessage3(productDto);
        verifyNoInteractions(kafkaConsumer);
    }


}
