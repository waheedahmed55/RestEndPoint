package com.learnvest.qacodechallenge.service.db;

import java.io.IOException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.commons.sql.SqlStatementsFileLoader;
import com.learnvest.qacodechallenge.commons.test.TestUtils;
import com.learnvest.qacodechallenge.service.config.UnitTestConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UnitTestConfig.class)
public class CardDaoRowMapperUnitTest {

    @Autowired
    CardDaoRowMapper cardDaoRowMapper;

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlStatementsFileLoader sqlStatementsFileLoader;

    NamedParameterJdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws IOException {
        assertNotNull(cardDaoRowMapper);
        assertNotNull(dataSource);
        assertNotNull(sqlStatementsFileLoader);

        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        assertNotNull(jdbcTemplate);
    }

    /**
     * Verify that {@link CardDaoRowMapper#mapObject(Card)} and {@link CardDaoRowMapper#mapRow} are working correctly.
     */
    @Test
    public void cardDaoRowMapper() {
        // generate a card object with test values
        Card createCard = TestUtils.cardWithTestValues();

        // exercise mapObject
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(sqlStatementsFileLoader.sql("createCard"),
                new MapSqlParameterSource(cardDaoRowMapper.mapObject(createCard)), keyHolder);
        Long id = keyHolder.getKey().longValue();
        assertNotNull(id);

        // exercise mapRow
        Card readCard = this.jdbcTemplate.queryForObject(sqlStatementsFileLoader.sql("readCard"),
                new MapSqlParameterSource("id", id), cardDaoRowMapper);
        assertNotNull(readCard);

        // verify the created and read values are correct
        assertEquals(createCard, readCard);
    }

}
