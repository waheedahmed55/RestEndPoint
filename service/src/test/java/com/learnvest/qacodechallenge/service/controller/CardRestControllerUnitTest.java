package com.learnvest.qacodechallenge.service.controller;

import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnvest.qacodechallenge.commons.constants.RequestMappingConstants;
import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.commons.test.TestUtils;
import com.learnvest.qacodechallenge.service.config.UnitTestConfig;
import com.learnvest.qacodechallenge.service.config.WebMvcTestConfig;
import com.learnvest.qacodechallenge.service.db.CardDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UnitTestConfig.class, CardRestControllerUnitTest.Config.class, WebMvcTestConfig.class })
public class CardRestControllerUnitTest {

    @Configuration
    @ComponentScan(basePackages = {"com.learnvest.qacodechallenge.service.controller"}, resourcePattern = "**/CardRestController.class")
    public static class Config extends WebMvcConfigurerAdapter {
    }

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    CardDao cardDao;

    ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        assertNotNull(cardDao);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    /**
     * Verify that the {@link CardRestController#create} endpoint is working as expected using the test dispatcher within
     * {@link org.springframework.test.web.servlet.MockMvc} to mock the request and response cycle of a running application.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void createCard() throws Exception {
        // generate a test card value
        Card createCard = TestUtils.cardWithTestValues();
        assertNull(createCard.getId());

        // send the test card value as JSON to the create endpoint
        RequestBuilder request = post(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCard));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_CREATED, response.getStatus());

        // map the create endpoint response to a new card object
        Card responseCard = mapper.readValue(response.getContentAsString(), Card.class);

        // verify that the create endpoint performed as expected by comparing input and output values
        assertNotNull(responseCard);
        assertNotNull(responseCard.getId());
        assertEquals(createCard, responseCard);

        // use the DAO to confirm the record was correctly created
        Card verifyCreateCard = cardDao.read(responseCard.getId());
        assertNotNull(verifyCreateCard);
        assertEquals(createCard, verifyCreateCard);
    }

    /**
     * Verify that {@link CardRestController#create} endpoint correctly responds with
     * {@link HttpServletResponse#SC_BAD_REQUEST} when a request to create a null object is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void createCardUsingNull() throws Exception {
        // send the null card value as JSON to the create endpoint
        RequestBuilder request = post(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(null));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    /**
     * Verify that {@link CardRestController#create} correctly responds with {@link HttpServletResponse#SC_PRECONDITION_FAILED}
     * when a request to create a {@link Card} with a non-null {@link Card#id} value is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void createCardNonNullCardId() throws Exception {
        // generate a test card value with an id already defined
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setId(new Random().longs(1L, Long.MAX_VALUE).findAny().getAsLong());

        RequestBuilder request = post(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCard));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_PRECONDITION_FAILED, response.getStatus());
    }

    /**
     * Verify that {@link CardRestController#create} is working correctly when a request for a {@link Card} that
     * contains a value which exceeds the {@link CardDao#create} database configuration is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test(expected = Exception.class)
    public void createCardColumnTooLong() throws Exception {
        // generate a test card value with a column that will exceed the database configuration
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setCardName(RandomStringUtils.randomAlphabetic(2000));

        RequestBuilder request = post(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCard));
        mockMvc.perform(request).andReturn().getResponse();
    }

    /**
     * Verify that the {@link CardRestController#read} endpoint is working as expected using the test dispatcher within
     * {@link org.springframework.test.web.servlet.MockMvc} to mock the request and response cycle of a running application.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void readCard() throws Exception {
        // create a test card in the database
        Card testCard = TestUtils.cardWithTestValues();
        assertNull(testCard.getId());

        Long id = cardDao.create(testCard);
        assertNotNull(id);
        assertEquals(id, testCard.getId());

        // call the read endpoint to read that created test card
        RequestBuilder request = get(RequestMappingConstants.Service.CARD + "/" + testCard.getId())
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_OK, response.getStatus());

        // map the read endpoint response to a new card object
        Card responseCard = mapper.readValue(response.getContentAsString(), Card.class);

        // verify that the read endpoint performed as expected by comparing input and output values
        assertNotNull(responseCard);
        assertEquals(testCard.getId(), responseCard.getId());
        assertEquals(testCard, responseCard);
    }

    /**
     * Verify that {@link CardRestController#read} correctly responds with {@link HttpServletResponse#SC_NOT_FOUND}
     * when a request for a non-existent {@link Card#id} is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void readCardNonExistent() throws Exception {
        // create a random card id that will not be in our local database
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();

        RequestBuilder request = get(RequestMappingConstants.Service.CARD + "/" + id)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    /**
     * Verify that the {@link CardRestController#update} endpoint is working as expected using the test dispatcher within
     * {@link org.springframework.test.web.servlet.MockMvc} to mock the request and response cycle of a running application.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test
    public void updateCard() throws Exception {
        // create a test card in the database
        Card createCard = TestUtils.cardWithTestValues();
        assertNull(createCard.getId());

        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertEquals(id, createCard.getId());

        // call the update endpoint to update that created test card
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(id);

        RequestBuilder request = put(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateCard));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_OK, response.getStatus());

        // map the update endpoint response to a new card object
        Card responseCard = mapper.readValue(response.getContentAsString(), Card.class);

        // verify that the update endpoint performed as expected by comparing input and output values
        assertNotNull(responseCard);
        assertEquals(updateCard.getId(), responseCard.getId());
        assertEquals(updateCard, responseCard);
    }

    /**
     * Verify that {@link CardRestController#update} correctly responds with {@link HttpServletResponse#SC_BAD_REQUEST}
     * when a request to update a {@link Card} with a null {@link Card#id} value is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test
    public void updateCardNull() throws Exception {
        RequestBuilder request = put(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(null));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    /**
     * Verify that {@link CardRestController#update} correctly responds with {@link HttpServletResponse#SC_PRECONDITION_FAILED}
     * when a request to update a {@link Card} with a null {@link Card#id} value is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test
    public void updateCardNullId() throws Exception {
        // generate a test card value with an id already defined
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(null);

        RequestBuilder request = put(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateCard));
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_PRECONDITION_FAILED, response.getStatus());
    }

    /**
     * Verify that {@link CardRestController#update} is working correctly when a request for a {@link Card} that
     * contains a value which exceeds the {@link CardDao#update} database configuration is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test(expected = Exception.class)
    public void updateCardColumnTooLong() throws Exception {
        // create a test card via the DAO
        Card testCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(testCard);
        assertNotNull(id);
        assertEquals(id, testCard.getId());

        // generate a test card value with a column that will exceed the database configuration
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(id);
        updateCard.setCardName(RandomStringUtils.randomAlphabetic(2000));

        RequestBuilder request = put(RequestMappingConstants.Service.CARD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateCard));
        mockMvc.perform(request).andReturn().getResponse();
    }

    /**
     * Verify that the {@link CardRestController#delete} endpoint is working as expected using the test dispatcher within
     * {@link org.springframework.test.web.servlet.MockMvc} to mock the request and response cycle of a running application.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test
    public void deleteCard() throws Exception {
        // create and verify test card in the database
        Card testCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(testCard);
        assertNotNull(id);
        assertEquals(id, testCard.getId());

        Card verifyTestCard = cardDao.read(id);
        assertEquals(testCard, verifyTestCard);

        // call the delete endpoint to delete that created test card
        RequestBuilder request = delete(RequestMappingConstants.Service.CARD + "/" + id)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_OK, response.getStatus());

        // verify that the card was deleted
        assertNull(cardDao.read(id));
    }

    /**
     * Verify that {@link CardRestController#delete} correctly responds with {@link HttpServletResponse#SC_NOT_FOUND}
     * when a request for a non-existent {@link Card#id} is made.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Ignore
    @Test
    public void deleteCardNonExistent() throws Exception {
        // create a random card id that will not be in our local database
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();

        // ensure that the CardRestController delete endpoint is performing as expected
        RequestBuilder request = delete(RequestMappingConstants.Service.CARD + "/" + id)
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

}
