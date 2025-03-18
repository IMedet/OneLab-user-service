package kz.medet.userservice;

import kz.medet.userservice.dto.CustomerResponseDto;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import kz.medet.userservice.mapper.CustomerMapper;
import kz.medet.userservice.repository.CustomerRepository;
import kz.medet.userservice.repository.CustomerSearchRepository;
import kz.medet.userservice.service.impl.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerSearchRepository customerSearchRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private List<Customer> customers;
    private List<CustomerDocument> customerDocuments;

    @BeforeEach
    void setUp() {
        Customer customer1 = new Customer(1L, "John", "Doe", 101L);
        Customer customer2 = new Customer(2L, "Jane", "Doe", null);
        customers = List.of(customer1, customer2);

        CustomerDocument doc1 = new CustomerDocument("1", "John", "Doe", 101L);
        CustomerDocument doc2 = new CustomerDocument("2", "Jane", "Doe", null);
        customerDocuments = List.of(doc1, doc2);
    }

    @Test
    void testFilterCustomers() {
        CustomerDocument customer1 = new CustomerDocument("1", "John", "Doe", 123L);
        CustomerDocument customer2 = new CustomerDocument("2", "Jane", "Smith", 456L);
        List<CustomerDocument> customers = List.of(customer1, customer2);

        when(customerSearchRepository.findAll()).thenReturn(customers);

        List<CustomerDocument> result = customerService.filterCustomers("John", 123L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void compareStreamPerformance_ShouldReturnExecutionTimes() {
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponseDto(any(Customer.class))).thenReturn(new CustomerResponseDto());

        Map<String, Long> result = customerService.compareStreamPerformance();

        assertNotNull(result);
        assertTrue(result.containsKey("sequential"));
        assertTrue(result.containsKey("parallel"));
    }

    @Test
    void filterCustomers_ShouldFilterBasedOnQueryAndOrderId() {
        when(customerSearchRepository.findAll()).thenReturn(customerDocuments);

        List<CustomerDocument> result = customerService.filterCustomers("John", 101L);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void groupCustomersByLastName_ShouldGroupCustomers() {
        when(customerRepository.findAll()).thenReturn(customers);

        Map<String, List<Customer>> result = customerService.groupCustomersByLastName();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get("Doe").size());
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
    }

    @Test
    void countAllCustomers_ShouldReturnCorrectCount() {
        when(customerRepository.findAll()).thenReturn(customers);

        long count = customerService.countAllCustomers();

        assertEquals(2, count);
    }

    @Test
    void countCustomersWithOrders_ShouldReturnCorrectCount() {
        when(customerRepository.findAll()).thenReturn(customers);

        long count = customerService.countCustomersWithOrders();

        assertEquals(1, count);
    }


    @Test
    void getAllCustomer_ShouldReturnFilteredCustomers() {
        lenient().when(customerRepository.findAll()).thenReturn(customers);
        lenient().when(customerSearchRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(customerDocuments);
        lenient().when(customerRepository.findAllById(anySet())).thenReturn(customers);

        when(customerMapper.toResponseDto(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer customer = invocation.getArgument(0);
                    Long orderId = customer.getOrderId() != null ? customer.getOrderId() : 0L;
                    return new CustomerResponseDto(customer.getId(), customer.getFirstName(), customer.getLastName(), orderId);
                });

        List<CustomerResponseDto> result = customerService.getAllCustomer("John", 101L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }



    @Test
    void getAllCustomer_ShouldReturnAllCustomers_WhenQueryIsNullOrBlank() {
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponseDto(any(Customer.class))).thenReturn(new CustomerResponseDto());

        List<CustomerResponseDto> result = customerService.getAllCustomer("", null, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}

