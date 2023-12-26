package com.arnold.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    CustomerRowMapper underTest;

    @Mock
    ResultSet rs;

    @BeforeEach
    void setUp() {
        underTest= new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        int rowNum= 1;
        final int id = 8;
        final String name = "Fred";
        final String email = "fred@gmail.com";
        final int age = 25;

        when(rs.getInt("id")).thenReturn(id);
        when(rs.getString("name")).thenReturn(name);
        when(rs.getString("email")).thenReturn(email);
        when(rs.getInt("age")).thenReturn(age);

        final Customer customer = (Customer) underTest.mapRow(rs, rowNum);

        assertEquals(id,customer.getId());
        assertEquals(name,customer.getName());
        assertEquals(email,customer.getEmail());
        assertEquals(age,customer.getAge());
    }
}