package com.moskalenko.application.dao;

import com.moskalenko.api.beans.BankCardStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BankCardStatusesDAO {

    private final NamedParameterJdbcTemplate jdbc;

    public BankCardStatusesDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Integer loadIdentifierFromDB(BankCardStatus status) {

        final String SQL = "WITH SELECTED AS ( SELECT id FROM bank_card_statuses WHERE name = :name), " +
                "INSERTED AS (INSERT INTO bank_card_statuses (name) " +
                "SELECT :name WHERE NOT EXISTS (SELECT 1 FROM SELECTED) RETURNING id) " +
                "SELECT id FROM SELECTED UNION ALL SELECT id FROM INSERTED";
        final MapSqlParameterSource params = new MapSqlParameterSource("name", status.name());
        return jdbc.queryForObject(SQL, params, Integer.class);
    }

    public BankCardStatus getBankCardStatusFromDB(Integer id) {
        final String SQL = "SELECT name FROM bank_card_statuses WHERE id = :id";
        final MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.queryForObject(SQL, params, BankCardStatus.class);
    }
}
