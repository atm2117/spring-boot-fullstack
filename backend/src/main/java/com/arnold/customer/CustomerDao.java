package com.arnold.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existCustomerWithEmail(String email);
    void deleteCustomerById(Customer customer);
    void updateCustomerById(Customer customer);
}
