package com.arnold.customer;

import com.arnold.exceptions.DuplicateResourceException;
import com.arnold.exceptions.NoChange;
import com.arnold.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Customer with id: %s, was not found.".formatted(id))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest ){
        //check if email already exists
        if(customerDao.existCustomerWithEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourceException("Customer with the provided email already exist.");
        }
        //if email doesn't exist, add the new customer
        Customer customer= new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(Integer id){
        Customer customer= getCustomerById(id);
        customerDao.deleteCustomerById(customer);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest){
        Customer customer= getCustomerById(id);

        boolean changes= false;

        if(customerUpdateRequest.name()!=null && !customerUpdateRequest.name().equals(customer.getName())){
            changes= true;
            customer.setName(customerUpdateRequest.name());
        }

        if(customerUpdateRequest.email()!=null && !customerUpdateRequest.email().equals(customer.getEmail())){
            if(customerDao.existCustomerWithEmail(customerUpdateRequest.email())){
                throw new DuplicateResourceException("Customer with the provided email already exist.");
            }
            changes= true;
            customer.setEmail(customerUpdateRequest.email());
        }

        if(customerUpdateRequest.age()!=null && !customerUpdateRequest.age().equals(customer.getAge())){
            changes= true;
            customer.setAge(customerUpdateRequest.age());
        }

        if(!changes){
            throw new NoChange("An existing value has been added.");
        }

        customerDao.updateCustomerById(customer);
    }
}
