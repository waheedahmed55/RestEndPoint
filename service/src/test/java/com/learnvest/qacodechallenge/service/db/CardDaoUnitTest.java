package com.learnvest.qacodechallenge.service.db;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.commons.test.TestUtils;
import com.learnvest.qacodechallenge.service.config.UnitTestConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UnitTestConfig.class)
public class CardDaoUnitTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CardDao cardDao;

    @Before
    public void setUp() {
        assertNotNull(cardDao);
        assertNotNull(cardDao.sql("createCard"));
        assertNotNull(cardDao.sql("readCard"));
        assertNotNull(cardDao.sql("updateCard"));
        assertNotNull(cardDao.sql("deleteCard"));
    }

    /**
     * Verify that {@link CardDao#create} is working correctly.
     */
    @Test
    public void create() {
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertEquals(id, createCard.getId());
    }

    /**
     * Verify that {@link CardDao#create} is working correctly when a request for creating a null object is made.
     */
    @Test(expected = RuntimeException.class)
    public void createNullCard() {
        cardDao.create(null);
    }

    /**
     * Verify that {@link CardDao#create} is working correctly when a request for a {@link Card} with a non-null id is made.
     */
    @Test(expected = RuntimeException.class)
    public void createNonNullCardId() {
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setId(new Random().longs(1L, Long.MAX_VALUE).findAny().getAsLong());
        cardDao.create(createCard);
    }

    /**
     * Verify that {@link CardDao#create} is working correctly when a request for a {@link Card} that contains a value
     * which exceeds the database configuration is made.
     */
    @Test(expected = RuntimeException.class)
    public void createCardColumnTooLong() {
        // generate a test card value with a column that will exceed the database configuration
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setCardName(RandomStringUtils.randomAlphabetic(2000));
        cardDao.create(createCard);
    }

    /**
     * Verify that {@link CardDao#read} is working correctly.
     */
    @Test
    public void read() {
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertNotNull(createCard.getId());

        Card readCard = cardDao.read(createCard.getId());
        assertNotNull(readCard);
        assertEquals(createCard.getId(), readCard.getId());
        assertEquals(createCard, readCard);
    }

    /**
     * Verify that {@link CardDao#read} is working correctly when a request for a non-existent {@link Card#id} is made.
     */
    @Test
    public void readNonExistentCard() {
        // create a random card id that will not be in our local database
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();
        Card card = cardDao.read(id);
        assertNull(card);
    }

    /**
     * Verify that {@link CardDao#update} is working correctly.
     */
    @Ignore
    @Test
    public void update() {
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertNotNull(createCard.getId());

        Card verifyCreateCard = cardDao.read(createCard.getId());
        assertNotNull(verifyCreateCard);
        assertEquals(createCard.getId(), verifyCreateCard.getId());
        assertEquals(createCard, verifyCreateCard);

        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(createCard.getId());
        cardDao.update(updateCard);

        Card verifyUpdateCard = cardDao.read(updateCard.getId());
        assertNotNull(verifyUpdateCard);
        assertEquals(createCard.getId(), verifyUpdateCard.getId());
        assertEquals(updateCard.getCardName(), verifyUpdateCard.getCardName());
        assertEquals(updateCard.getCardNumber(), verifyUpdateCard.getCardNumber());
        assertTrue(Arrays.equals(updateCard.getCardImage(), verifyUpdateCard.getCardImage()));
        assertEquals(updateCard.getCardImageMimeType(), verifyUpdateCard.getCardImageMimeType());
        assertEquals(updateCard.getCardType(), verifyUpdateCard.getCardType());
    }

    /**
     * Verify that {@link CardDao#update} is working correctly when a request for creating a null object is made.
     */
    @Test(expected = RuntimeException.class)
    public void updateNullCard() {
        cardDao.update(null);
    }

    /**
     * Verify that {@link CardDao#update} is working correctly when a request for a non-existent {@link Card#id} is made.
     */
    @Test(expected = RuntimeException.class)
    public void updateNonExistentCard() {
        // create a random card id that will not be in our local database
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong());
        cardDao.update(updateCard);
    }

    /**
     * Verify that {@link CardDao#update} is working correctly when a request for a {@link Card} that contains a value
     * which exceeds the database configuration is made.
     */
    @Test(expected = RuntimeException.class)
    public void updateCardColumnTooLong() {
        // generate a test card value with a column that will exceed the database configuration
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertNotNull(createCard.getId());

        Card verifyCreateCard = cardDao.read(createCard.getId());
        assertNotNull(verifyCreateCard);
        assertEquals(createCard.getId(), verifyCreateCard.getId());
        assertEquals(createCard, verifyCreateCard);

        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(createCard.getId());
        updateCard.setCardName(RandomStringUtils.randomAlphabetic(2000));
        cardDao.update(updateCard);
    }

    /**
     * Verify that {@link CardDao#delete} is working correctly.
     */
    @Test
    public void delete() {
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertNotNull(createCard.getId());

        Card verifyCreateCard = cardDao.read(createCard.getId());
        assertNotNull(verifyCreateCard);
        assertEquals(createCard.getId(), verifyCreateCard.getId());
        assertEquals(createCard, verifyCreateCard);

        cardDao.delete(createCard.getId());

        Card verifyDeleteCard = cardDao.read(createCard.getId());
        assertNull(verifyDeleteCard);
    }

    /**
     * Verify that {@link CardDao#delete} is working correctly when a request for a non-existent {@link Card#id} is made.
     */
    @Test(expected = RuntimeException.class)
    public void deleteNonExistentCard() {
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();
        cardDao.delete(id);
    }

}
