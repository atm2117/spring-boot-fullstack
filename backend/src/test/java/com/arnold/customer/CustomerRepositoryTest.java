package com.arnold.customer;

import com.arnold.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setup(){
        System.out.println(applicationContext.getBeanDefinitionCount());
    }
    @Test
    void existCustomerWithEmail(){
        final String email = "arnold@jpm.com";
        Customer customer= new Customer(
                "Arnold",
                email,
                29
        );

        underTest.save(customer);

        final String actualEmail = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(c -> c.getEmail())
                .orElseThrow();

        assertEquals(email,actualEmail);

        final boolean actual = underTest.existsCustomerByEmail(email);
        assertThat(actual).isTrue();
    }
}
