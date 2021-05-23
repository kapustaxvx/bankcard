package com.moskalenko.application.dao;

import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.beans.CounterpartyBuilder;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class CounterpartyDAO {

    private final NamedParameterJdbcTemplate jdbc;


    public CounterpartyDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public Counterparty createCounterparty(CounterpartyDescriptionRequest description, String accountNumber) {

        final String SQLInsertCounterparty = "INSERT INTO counterparties (description) " +
                "VALUES (:description) RETURNING id";

        final String SQLInsertAccount = "INSERT INTO accounts (account_number) " +
                "VALUES (:account_number) RETURNING id";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("description", description.getDescription());
        params.addValue("account_number", accountNumber);
        final Long counterpartyId = jdbc.queryForObject(SQLInsertCounterparty, params, Long.class);
        final Long accountId = jdbc.queryForObject(SQLInsertAccount, params, Long.class);

        final String SQLCounterpartySelect = "SELECT id, description, version FROM counterparties " +
                "WHERE id = :counterparty_id";

        final String SQLInsertCounterpartiesAccount = "INSERT INTO counterparty_accounts (counterparty_id, account_id) " +
                "VALUES (:counterparty_id, :account_id)";

        final MapSqlParameterSource paramsForCounterpartiesAccount = new MapSqlParameterSource();
        paramsForCounterpartiesAccount.addValue("counterparty_id", counterpartyId);
        paramsForCounterpartiesAccount.addValue("account_id", accountId);
        jdbc.update(SQLInsertCounterpartiesAccount, paramsForCounterpartiesAccount);

        return jdbc.queryForObject(SQLCounterpartySelect, paramsForCounterpartiesAccount, new CounterpartyMapper());
    }

    public Collection<Counterparty> getCounterparties() {
        final String SQL = "SELECT id, description, version FROM counterparties";
        return jdbc.query(SQL, new CounterpartyMapper());
    }


    class CounterpartyMapper implements RowMapper<Counterparty> {
        @Override
        public Counterparty mapRow(ResultSet resultSet, int i) throws SQLException {
            return CounterpartyBuilder.create()
                    .withId(resultSet.getLong("id"))
                    .withDescription(resultSet.getString("description"))
                    .withVersion(resultSet.getLong("version"))
                    .build();
        }
    }
}
