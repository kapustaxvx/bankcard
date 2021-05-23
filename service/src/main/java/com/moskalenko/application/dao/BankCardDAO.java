package com.moskalenko.application.dao;

import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.BankCardBuilder;
import com.moskalenko.api.beans.BankCardStatus;
import com.moskalenko.application.exceptions.VersionMismatchException;
import com.moskalenko.application.service.beans.BankCardWithAccount;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
public class BankCardDAO {
    private final NamedParameterJdbcTemplate jdbc;
    private final BankCardStatusesDAO bankCardStatusesDAO;

    public BankCardDAO(NamedParameterJdbcTemplate jdbc, BankCardStatusesDAO bankCardStatusesDAO) {
        this.jdbc = jdbc;
        this.bankCardStatusesDAO = bankCardStatusesDAO;
    }

    public BankCard createBankCard(Long accountId, String cardNumber) {

        final String SQL = "INSERT INTO bank_cards (account_id, card_number, status_id) " +
                "VALUES (:account_id, :card_number, :status) " +
                "RETURNING id, card_number, status_id, version";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_id", accountId);
        params.addValue("card_number", cardNumber);
        params.addValue("status", bankCardStatusesDAO.loadIdentifierFromDB(BankCardStatus.NEW));
        return jdbc.queryForObject(SQL, params, new BankCardMapper());
    }

    public Collection<BankCard> getBankCards(Long customerId) {

        final String SQL = "SELECT bc.id, bc.card_number, bc.status_id, bc.version " +
                "FROM customer_accounts " +
                "JOIN accounts a ON a.id = customer_accounts.account_id " +
                "JOIN bank_cards bc ON a.id = bc.account_id " +
                "WHERE customer_accounts.customer_id = :customer_id";

        final MapSqlParameterSource params = new MapSqlParameterSource("customer_id", customerId);
        return jdbc.query(SQL, params, new BankCardMapper());
    }

    public BankCard confirmBankCardCreation(BankCard bankCard) {

        final String SQL = "UPDATE bank_cards " +
                "SET status_id=:status, version = version + 1 " +
                "WHERE id=:bank_card_id AND version = :version " +
                "RETURNING id, card_number, status_id, version";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bank_card_id", bankCard.getId());
        params.addValue("status", bankCardStatusesDAO.loadIdentifierFromDB(BankCardStatus.CONFIRMED));
        params.addValue("version", bankCard.getVersion());
        try {
            final BankCard bankCardToReturn = jdbc.queryForObject(SQL, params, new BankCardMapper());
            return bankCardToReturn;
        } catch (EmptyResultDataAccessException e) {
            throw new VersionMismatchException("Невозможно подтвердить создание банковской карты");
        }
    }


    public Optional<BankCard> getBankCardById(Long id) {
        final String SQL = "SELECT id, card_number, status_id, version FROM bank_cards WHERE id= :id";
        final MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL, params, new BankCardMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<BankCardWithAccount> getBankCardWithAccount(Long id) {
        final String SQL = "SELECT id, account_id, card_number, status_id, version FROM bank_cards WHERE id= :id";
        final MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL, params, new BankCardWithAccountMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    class BankCardMapper implements RowMapper<BankCard> {

        @Override
        public BankCard mapRow(ResultSet resultSet, int i) throws SQLException {
            return BankCardBuilder.create()
                    .withId(resultSet.getLong("id"))
                    .withMaskedCardNumber(resultSet.getString("card_number").substring(0, 4) + "********" +
                            resultSet.getString("card_number").substring(4 + 8))
                    .withStatus(bankCardStatusesDAO.getBankCardStatusFromDB(resultSet.getInt("status_id")))
                    .withVersion(resultSet.getLong("version"))
                    .build();
        }
    }

    class BankCardWithAccountMapper implements RowMapper<BankCardWithAccount> {

        @Override
        public BankCardWithAccount mapRow(ResultSet resultSet, int i) throws SQLException {
            return new BankCardWithAccount(resultSet.getLong("id"),
                    resultSet.getLong("account_id"),
                    resultSet.getString("card_number"),
                    bankCardStatusesDAO.getBankCardStatusFromDB(resultSet.getInt("status_id")),
                    resultSet.getLong("version"));
        }
    }

}
