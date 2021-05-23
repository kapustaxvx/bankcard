package com.moskalenko.application.dao;

import com.moskalenko.application.exceptions.VersionMismatchException;
import com.moskalenko.application.service.beans.Account;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AccountDAO {

    private final NamedParameterJdbcTemplate jdbc;

    public AccountDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public Account createAccount(Long customerId, String accountNumber) {

        final String SQLInsertAccount = "INSERT INTO accounts (account_number) " +
                "VALUES (:account_number) RETURNING id";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_number", accountNumber);

        final Long accountId = jdbc.queryForObject(SQLInsertAccount, params, Long.class);


        final String SQLInsertCustomerAccount = "INSERT INTO customer_accounts (customer_id, account_id) " +
                "VALUES (:customer_id, :account_id)";

        final String SQLSelectAccount = "SELECT id, account_number, debit, credit, version " +
                "FROM accounts WHERE id = :account_id";

        final MapSqlParameterSource paramsInsertCustomerAccount = new MapSqlParameterSource();
        paramsInsertCustomerAccount.addValue("customer_id", customerId);
        paramsInsertCustomerAccount.addValue("account_id", accountId);
        jdbc.update(SQLInsertCustomerAccount, paramsInsertCustomerAccount);

        return jdbc.queryForObject(SQLSelectAccount, paramsInsertCustomerAccount, new AccountMapper());
    }

    public void confirmIncreaseBalance(Account account) {

        final String SQL = "UPDATE accounts " +
                "SET  debit = :debit, " +
                "version = version + 1 " +
                "WHERE id = :id AND version = :version";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", account.getId());
        params.addValue("debit", account.getDebit());
        params.addValue("version", account.getVersion());

        int status = jdbc.update(SQL, params);
        if (status == 0) throw new VersionMismatchException("Некоректная версия");
    }

    public void confirmDecreaseBalance(Account account) {
        final String SQL = "UPDATE accounts " +
                "SET  credit = :credit, " +
                "version = version + 1 " +
                "WHERE id = :id AND version = :version";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", account.getId());
        params.addValue("credit", account.getCredit());
        params.addValue("version", account.getVersion());

        int status = jdbc.update(SQL, params);
        if (status == 0) throw new VersionMismatchException("Невозможно подтвердить списание");

    }


    public Optional<Account> getAccount(Long id) {
        final String SQLSelectAccount = "SELECT id, account_number, debit , credit, version " +
                "FROM accounts WHERE id = :id";
        final MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQLSelectAccount, params, new AccountMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    class AccountMapper implements RowMapper<Account> {

        @Override
        public Account mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Account(resultSet.getLong("id"),
                    resultSet.getString("account_number"),
                    resultSet.getBigDecimal("debit"),
                    resultSet.getBigDecimal("credit"),
                    resultSet.getLong("version"));
        }
    }

}
