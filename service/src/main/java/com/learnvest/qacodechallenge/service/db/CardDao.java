package com.learnvest.qacodechallenge.service.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.learnvest.qacodechallenge.commons.db.BaseDao;
import com.learnvest.qacodechallenge.commons.model.card.Card;

public class CardDao extends BaseDao<Card> {

    private static final Logger LOG = LoggerFactory.getLogger(CardDao.class);

    /**
     * Create a {@link Card} object.
     * @param card {@link Card}
     * @return {@link Card}
     */
    @Override
    public long create(Card card) {
        // validate input
        if (card == null) {
            throw new RuntimeException("Request to create a new Card received null");
        } else if (card.getId() != null) {
            throw new RuntimeException("When creating a new Card the id should be null, but was set to " + card.getId());
        }

        LOG.trace("Creating card {}", card);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = this.jdbcTemplate.update(sql("createCard"), new MapSqlParameterSource(rowMapper.mapObject(card)), keyHolder);

        if (result != 1) {
            throw new RuntimeException("Failed attempt to create card " + card.toString() + " affected " + result + " rows");
        }

        Long id = keyHolder.getKey().longValue();
        card.setId(id);
        return id;
    }

    /**
     * Retrieve a {@link Card} object by its {@link Card#id}.
     * @param id long
     * @return {@link Card}
     */
    @Override
    public Card read(long id) {
        LOG.trace("Reading card {}", id);
        try {
            return (Card) this.jdbcTemplate.queryForObject(sql("readCard"), new MapSqlParameterSource("id", id), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Update the provided {@link Card} object.
     * @param card {@link Card}
     */
    @Override
    public void update(Card card) {
        if (card == null) {
            throw new RuntimeException("Request to update a Card received null");
        } else if (card.getId() == null) {
            throw new RuntimeException("When updating a Card the id should not be null");
        }

        LOG.trace("Updating card {}", card);
        int result = this.jdbcTemplate.update(sql("updateCard"), new MapSqlParameterSource(rowMapper.mapObject(card)));

        if (result != 1) {
            throw new RuntimeException("Failed attempt to update card " + card.toString() + " affected " + result + " rows");
        }
    }

    /**
     * Delete a {@link Card} object by its {@link Card#id}.
     * @param id long
     */
    @Override
    public void delete(long id) {
        LOG.trace("Deleting card {}", id);
        int result = this.jdbcTemplate.update(sql("deleteCard"), new MapSqlParameterSource("id", id));
        if (result != 1) {
            throw new RuntimeException("Failed attempt to update card " + id + " affected " + result + " rows");
        }
    }

}
