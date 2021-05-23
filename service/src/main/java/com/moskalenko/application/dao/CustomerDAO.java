package com.moskalenko.application.dao;

import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.beans.CustomerBuilder;
import com.moskalenko.api.requests.CustomerCreatingRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerDAO {

    private final NamedParameterJdbcTemplate jdbc;

    public CustomerDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Customer createCustomer(CustomerCreatingRequest customerRequest) {
        final String SQL = "INSERT INTO customers (first_name, second_name) " +
                "VALUES (:first_name, :second_name) RETURNING id, first_name, second_name, version";
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("first_name", customerRequest.getFirstName());
        params.addValue("second_name", customerRequest.getSecondName());
        return jdbc.queryForObject(SQL, params, new CustomerMapper());
    }

    public Optional<Customer> getCustomerById(Long id) {

        final String SQL = "SELECT id, first_name, second_name, version FROM customers " +
                "WHERE id= :id";
        final MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL, params, new CustomerMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    class CustomerMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
            return CustomerBuilder.create()
                    .withId(resultSet.getLong("id"))
                    .withFirstName(resultSet.getString("first_name"))
                    .withSecondName(resultSet.getString("second_name"))
                    .withVersion(resultSet.getLong("version"))
                    .build();
        }
    }
}
