package com.arnold.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerDaoJDBCImpl implements CustomerDao{

    private JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerDaoJDBCImpl(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql= """
                Select id,name,email,age 
                FROM customer
                """;

        return jdbcTemplate.query(sql, customerRowMapper);

    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sql= """
                Select id,name,email,age
                FROM customer
                WHERE id=?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql= """
                INSERT INTO customer(name,email,age) VALUES(?,?,?)
                """;
        int result= jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existCustomerWithEmail(String email) {
        String sql= """
                SELECT count(id)
                FROM customer
                WHERE email=?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count!=null && count>0;
    }

    @Override
    public void deleteCustomerById(Customer customer) {
        String sql= """
                DELETE FROM customer
                WHERE id=?
                """;
        jdbcTemplate.update(sql, customer.getId());
    }

    @Override
    public void updateCustomerById(Customer customer) {

        if(customer.getName()!=null){
            String sql= """
                    UPDATE customer
                    SET name= ?
                    WHERE id= ?
                    """;
            int result= jdbcTemplate.update(
                    sql,
                    customer.getName(),
                    customer.getId()
            );
        }

        if(customer.getEmail()!=null){
            String sql= """
                    UPDATE customer
                    SET email= ?
                    WHERE id= ?
                    """;
            int result= jdbcTemplate.update(
                    sql,
                    customer.getEmail(),
                    customer.getId()
            );
        }

        if(customer.getAge()!=null){
            String sql= """
                    UPDATE customer
                    SET age= ?
                    WHERE id= ?
                    """;
            int result= jdbcTemplate.update(
                    sql,
                    customer.getAge(),
                    customer.getId()
            );
        }
    }
}
