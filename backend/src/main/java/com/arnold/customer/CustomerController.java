package com.arnold.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(method= RequestMethod.GET)
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.GET)
    public Customer getCustomer(
            @PathVariable("customerId") Integer customerId){
        return customerService.getCustomerById(customerId);
    }

    @RequestMapping(method= RequestMethod.POST)
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.addCustomer(customerRegistrationRequest);
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.DELETE)
    public void removeCustomer(
            @PathVariable("customerId") Integer customerId){
        customerService.deleteCustomer(customerId);
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.PUT)
    public void updateCustomer(
            @PathVariable("customerId") Integer customerId,
            @RequestBody CustomerUpdateRequest customerUpdateRequest){
        customerService.updateCustomer(customerId,customerUpdateRequest);
    }
}
