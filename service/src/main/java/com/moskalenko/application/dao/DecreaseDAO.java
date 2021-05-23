package com.moskalenko.application.dao;

import com.moskalenko.application.exceptions.DecreaseBalanceException;
import com.moskalenko.application.service.beans.UnconfirmedDecrease;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class DecreaseDAO {

    private final NamedParameterJdbcTemplate jdbc;

    public DecreaseDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    @Transactional
    public void createInvoice(Long accountId, BigDecimal amount) {

        final Timestamp creationDate = Timestamp.from(Instant.now());

        final String SQLDecrease = "INSERT INTO decrease_invoices (account_id, amount, creation_date) " +
                "VALUES (:account_id, :amount, :creation_date)" +
                "RETURNING id";

        final String SQLUnconfirmedDecrease = "INSERT INTO unconfirmed_decreases(decrease_id) " +
                "VALUES (:decrease_id)";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_id", accountId);
        params.addValue("amount", amount);
        params.addValue("creation_date", creationDate);

        final MapSqlParameterSource decreaseParams = new MapSqlParameterSource("decrease_id",
                jdbc.queryForObject(SQLDecrease, params, Integer.class));
        int status = jdbc.update(SQLUnconfirmedDecrease, decreaseParams);
        if (status == 0) throw new DecreaseBalanceException("Невозможно создать зяавку на выплату");

    }


    public void removeFromUnconfirmedDecrease(Long decreaseId) {
        final String SQL = "DELETE FROM unconfirmed_decreases WHERE decrease_id = :decrease_id";
        final MapSqlParameterSource params = new MapSqlParameterSource("decrease_id", decreaseId);
        jdbc.update(SQL, params);
    }

    public Optional<UnconfirmedDecrease> getUnconfirmedDecrease(Long decreaseId) {
        final String SQL = "SELECT id, account_id, amount, creation_date, ud.decrease_id AS decrease_id " +
                "FROM decrease_invoices AS di " +
                "JOIN unconfirmed_decreases ud on di.id = ud.decrease_id  " +
                "WHERE di.id = :decreaseId";
        final MapSqlParameterSource params = new MapSqlParameterSource("decreaseId", decreaseId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL, params, new UnconfirmedDecreaseMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    class UnconfirmedDecreaseMapper implements RowMapper<UnconfirmedDecrease> {

        @Override
        public UnconfirmedDecrease mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UnconfirmedDecrease(resultSet.getLong("decrease_id"),
                    resultSet.getLong("account_id"),
                    resultSet.getBigDecimal("amount"),
                    resultSet.getTimestamp("creation_date").toInstant(),
                    resultSet.getLong("decrease_id"));
        }
    }
}
