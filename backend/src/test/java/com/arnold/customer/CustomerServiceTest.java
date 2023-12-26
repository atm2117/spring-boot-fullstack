package com.arnold.customer;

import com.arnold.exceptions.DuplicateResourceException;
import com.arnold.exceptions.NoChange;
import com.arnold.exceptions.ResourceNotFound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest= new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomerById() {
        final Integer id = 9;

        final Customer customer = new Customer(
                id, "John","john@gmail.com",23
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        final Customer actual = underTest.getCustomerById(id);

        assertEquals(customer,actual);
        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void getCustomerByIdThrows(){
        final Integer id = 10;


        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFound.class).hasMessage("Customer with id: %s, was not found.".formatted(id));
    }

    @Test
    void addCustomer() {
        String email= "vishnu@chase.com";

        when(customerDao.existCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request= new CustomerRegistrationRequest(
                "Vishnu",
                email,
                29
        );

        underTest.addCustomer(request);
        ArgumentCaptor<Customer> customerArgumentCaptor= ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer customer= customerArgumentCaptor.getValue();
        assertEquals(request.name(),customer.getName());
        assertEquals(request.email(),customer.getEmail());
        assertEquals(request.age(),customer.getAge());
    }

    @Test
    void addCustomerException(){
        String email= "venk@gmail.com";

        when(customerDao.existCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request= new CustomerRegistrationRequest(
                "Venk",
                email,
                29
        );

        assertThatThrownBy(()->
            underTest.addCustomer(request)).
                isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with the provided email already exist.");
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {
        Integer id= 12;
        final String email = "ryan@gmail.com";

        Customer customer= new Customer(id,"Ryan", email,32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        underTest.deleteCustomer(id);
        verify(customerDao).deleteCustomerById(customer);
    }

    @Test
    void updateCustomerName() {
        Integer id= 13;
        final String email = "josh@gmail.com";

        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                "Josh", email, 45
        );

        Customer customer= new Customer(
                id,
                "Joshua",
                customerUpdateRequest.email(),
                customerUpdateRequest.age()
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,customerUpdateRequest);

        verify(customerDao).updateCustomerById(customer);
    }

    @Test
    void updateCustomerEmail() {
        Integer id= 13;
        final String email = "josh@gmail.com";

        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                "Josh", email, 45
        );

        Customer customer= new Customer(
                id,
                customerUpdateRequest.name(),
                "joshua@gmail.com",
                customerUpdateRequest.age()
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,customerUpdateRequest);

        verify(customerDao).updateCustomerById(customer);
    }

    @Test
    void updateCustomerEmailException() {
        Integer id= 13;
        final String email = "josh@gmail.com";

        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                "Josh", email, 45
        );

        Customer customer= new Customer(
                id,
                customerUpdateRequest.name(),
                "joshua@gmail.com",
                customerUpdateRequest.age()
        );

        when(customerDao.existCustomerWithEmail(email)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(()->underTest.updateCustomer(id,customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with the provided email already exist.");

        verify(customerDao,never()).updateCustomerById(customer);
    }

    @Test
    void updateCustomerAge() {
        Integer id= 13;
        final String email = "josh@gmail.com";

        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                "Josh", email, 45
        );

        Customer customer= new Customer(
                id,
                customerUpdateRequest.name(),
                customerUpdateRequest.email(),
                50
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,customerUpdateRequest);

        verify(customerDao).updateCustomerById(customer);
    }

    @Test
    void updateCustomerNoChanges() {
        Integer id= 13;
        final String email = "josh@gmail.com";

        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                "Josh", email, 45
        );

        Customer customer= new Customer(
                id,
                customerUpdateRequest.name(),
                customerUpdateRequest.email(),
                customerUpdateRequest.age()
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(()->
                underTest.updateCustomer(id,customerUpdateRequest))
                .isInstanceOf(NoChange.class)
                .hasMessage("An existing value has been added.");


        verify(customerDao,never()).updateCustomerById(customer);
    }
}