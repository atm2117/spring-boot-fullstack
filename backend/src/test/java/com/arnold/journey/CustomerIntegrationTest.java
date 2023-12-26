package com.arnold.journey;

import com.arnold.customer.Customer;
import com.arnold.customer.CustomerRegistrationRequest;
import com.arnold.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private Random random= new Random();

    private static String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        //create a registration request
        Faker faker= new Faker();
        String name= String.valueOf(faker.name());
        String email= name+ "@gmail.com";
        int age= random.nextInt(1,50);

        CustomerRegistrationRequest request= new CustomerRegistrationRequest(
                name, email, age
        );

        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        final List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        //make sure that the customer is present
        Customer expectedCustomer= new Customer(
                name, email, age
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        //get a customer by id
        Integer id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteACustomer() {
        //create a registration request
        Faker faker= new Faker();
        String name= String.valueOf(faker.name());
        String email= name+ "@pgmail.com";
        int age= random.nextInt(1,50);

        CustomerRegistrationRequest request= new CustomerRegistrationRequest(
                name, email, age
        );

        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        final List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();


        //get id
        Integer id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();

        //delete customer by id
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //check if the customer is not present anymore
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create a registration request
        Faker faker= new Faker();
        String name= String.valueOf(faker.name());
        String email= name+ "@chase.com";
        int age= random.nextInt(1,50);

        CustomerRegistrationRequest request= new CustomerRegistrationRequest(
                name, email, age
        );

        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        final List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        //create customer request
        final String updatedName = name + "a";
        CustomerUpdateRequest customerUpdateRequest= new CustomerUpdateRequest(
                updatedName,email,age
        );


        //get id
        Integer id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c->c.getId())
                .findFirst()
                .orElseThrow();


        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get the customer and check if it has the updated value
        Customer expectedCustomer= new Customer(
                id, updatedName, email, age
        );

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);

    }
}
