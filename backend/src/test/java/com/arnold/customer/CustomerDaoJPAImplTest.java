package com.arnold.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerDaoJPAImplTest {

    private CustomerDaoJPAImpl underTest;

    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable= MockitoAnnotations.openMocks(this);
        underTest= new CustomerDaoJPAImpl(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //Invoke the selectCustomer method and verify that the find all was invoked
        underTest.selectAllCustomers();
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        Integer id= 7;
        underTest.selectCustomerById(id);

        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        final Customer james = new Customer(1, "James", "james@gmail.com", 7);
        underTest.insertCustomer(james);

        verify(customerRepository).save(james);
    }

    @Test
    void existCustomerWithEmail() {
        final String email = "fred@gmail.com";
        underTest.existCustomerWithEmail(email);

        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        final Customer james = new Customer(1, "James", "james@gmail.com", 7);
        underTest.deleteCustomerById(james);

        verify(customerRepository).delete(james);
    }

    @Test
    void updateCustomerById() {
        final Customer james = new Customer(1, "James", "james@gmail.com", 7);
        underTest.updateCustomerById(james);

        verify(customerRepository).save(james);
    }
}