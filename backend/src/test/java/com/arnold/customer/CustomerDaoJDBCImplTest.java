package com.arnold.customer;

import com.arnold.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerDaoJDBCImplTest extends AbstractTestcontainers {

    private CustomerDaoJDBCImpl underTest;
    private final CustomerRowMapper customerRowMapper= new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest= new CustomerDaoJDBCImpl(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Add a new customer to the db.
        Customer customer= new Customer(
                "Arnold",
                "arnold@jpm.com",
                29
        );

        underTest.insertCustomer(customer);
        List<Customer> actual= underTest.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Add a new customer to the db.
        final String email = "ian@jpm.com";
        Customer customer= new Customer(
                "Ian",
                email,
                20
        );

        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();

        final Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c->{
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(customer.getName());
           assertThat(c.getEmail()).isEqualTo(customer.getEmail());
           assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void selectCustomerByIdEmpty() {
        int id=-1;

        final Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        Customer customer= new Customer(
                "George",
                "george@jpm.com",
                25
        );

        underTest.insertCustomer(customer);

        List<Customer> actual= underTest.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void existCustomerWithEmail() {
        final String email = "peter@jpm.com";
        Customer customer= new Customer(
                "Peter",
                email,
                35
        );

        underTest.insertCustomer(customer);

        final boolean actual = underTest.existCustomerWithEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerWithEmailFalse() {
        final String email = "rdb@jpm.com";
        final boolean actual = underTest.existCustomerWithEmail(email);
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        final String email = "fred@jpm.com";
        Customer customer= new Customer(
                "Fred",
                email,
                38
        );

        underTest.insertCustomer(customer);

        Customer actualCustomer = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(actualCustomer);

        assertThat(underTest.selectCustomerById(id)).isNotPresent();
    }

    @Test
    void updateCustomerById() {
        final String email = "paul@jpm.com";
        Customer customer= new Customer(
                "Paul",
                email,
                28
        );

        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();

        Customer update= new Customer();
        update.setId(id);
        update.setName("Paulo");

        underTest.updateCustomerById(update);

        Customer actual = underTest.selectCustomerById(id).stream().findFirst().orElseThrow();
        assertEquals("Paulo",actual.getName());
    }
}