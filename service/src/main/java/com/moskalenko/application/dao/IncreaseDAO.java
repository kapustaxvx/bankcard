package com.moskalenko.application.dao;

import com.moskalenko.application.exceptions.IncreaseBalanceException;
import com.moskalenko.application.service.beans.UnconfirmedIncrease;
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
public class IncreaseDAO {
    private final NamedParameterJdbcTemplate jdbc;

    public IncreaseDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    @Transactional
    public void createInvoice(Long accountId, BigDecimal amount) {

        final Timestamp creationDate = Timestamp.from(Instant.now());

        final String SQLIncrease = "INSERT INTO increase_invoices (account_id, amount, creation_date) " +
                "VALUES (:account_id, :amount, :creation_date) " +
                "RETURNING id";

        final String SQLUnconfirmedIncrease = "INSERT INTO unconfirmed_increases(increase_id) " +
                "VALUES (:increase_id)";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_id", accountId);
        params.addValue("amount", amount);
        params.addValue("creation_date", creationDate);

        final MapSqlParameterSource increaseParams = new MapSqlParameterSource("increase_id",
                jdbc.queryForObject(SQLIncrease, params, Long.class));
        int status = jdbc.update(SQLUnconfirmedIncrease, increaseParams);
        if (status == 0) throw new IncreaseBalanceException("Невозможно создать заявку на пополнение");
    }

    public void removeFromUnconfirmedIncrease(Long increaseId) {
        final String SQL = "DELETE FROM unconfirmed_increases WHERE increase_id = :increase_id";
        final MapSqlParameterSource params = new MapSqlParameterSource("increase_id", increaseId);
        jdbc.update(SQL, params);
    }

    public Optional<UnconfirmedIncrease> getUnconfirmedIncrease(Long increaseId) {
        final String SQL = "SELECT id, account_id, amount, creation_date, ui.increase_id AS increase_id " +
                "FROM increase_invoices AS ii " +
                "JOIN unconfirmed_increases ui on ii.id = ui.increase_id  " +
                "WHERE ii.id = :increaseId";
        final MapSqlParameterSource params = new MapSqlParameterSource("increaseId", increaseId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL, params, new UnconfirmedIncreaseMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    class UnconfirmedIncreaseMapper implements RowMapper<UnconfirmedIncrease> {
        @Override
        public UnconfirmedIncrease mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UnconfirmedIncrease(resultSet.getLong("increase_id"),
                    resultSet.getLong("account_id"),
                    resultSet.getBigDecimal("amount"),
                    resultSet.getTimestamp("creation_date").toInstant(),
                    resultSet.getLong("increase_id"));
        }
    }
}
