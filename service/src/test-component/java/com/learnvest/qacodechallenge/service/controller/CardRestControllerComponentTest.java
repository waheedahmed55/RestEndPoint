package com.learnvest.qacodechallenge.service.controller;

import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.learnvest.qacodechallenge.commons.constants.RequestMappingConstants;
import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.commons.model.error.ErrorSummary;
import com.learnvest.qacodechallenge.commons.test.TestUtils;
import com.learnvest.qacodechallenge.service.Service;
import com.learnvest.qacodechallenge.service.db.CardDao;
import com.learnvest.qacodechallenge.service.error.ServiceErrorHandler;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This test class exercises the {@link CardDao} functions as well as all of the {@link CardRestController} endpoints
 * and the corresponding {@link CardDao} functions in a fully end-to-end fashion in a running spring boot instance
 * that is backed by the database configured within test.properties.
 * <dl>
 *     <dt>{@link CardRestController}</dt>
 *     <dl>{@link CardRestController#create}</dl>
 *     <dl>{@link CardRestController#read}</dl>
 *     <dl>{@link CardRestController#update}</dl>
 *     <dl>{@link CardRestController#delete}</dl>
 *     <dt>{@link CardDao}</dt>
 *     <dl>{@link CardDao#create}</dl>
 *     <dl>{@link CardDao#read}</dl>
 *     <dl>{@link CardDao#update}</dl>
 *     <dl>{@link CardDao#delete}</dl>
 * </dl>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Service.class)
@TestPropertySource(locations="classpath:test.properties")
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class CardRestControllerComponentTest {

    @Configuration
    static class Config {

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

    }

    @Value("${local.server.port}")
    int port;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CardDao cardDao;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        assertNotNull(restTemplate);
        assertNotNull(cardDao);

        String baseURI = RestAssured.baseURI;
        RestAssured.port = port;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseURI);
        builder.port(port);
    }

    /**
     * The following test verifies that {@link CardRestController#create} endpoint is working correctly.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void createCard() throws Exception {
        // generate a card object with random bits of data for use within our test
        Card createCard = TestUtils.cardWithTestValues();

        // ensure that the CardRestController create endpoint is working correctly by sending it proper JSON
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(createCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        ExtractableResponse<Response> createResponse = given()
                .when()
                .spec(requestSpecification)
                .post(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_CREATED).extract();

        // map the endpoint response to a card object
        Card cardResponse = mapper.readValue(createResponse.response().body().asString(), Card.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(cardResponse);
        assertNotNull(cardResponse.getId());
        assertEquals(createCard, cardResponse);

        // use the DAO to verify that the card was created
        Card verifyCreateCard = cardDao.read(cardResponse.getId());
        assertEquals(cardResponse, verifyCreateCard);
    }

    /**
     * Verify that {@link CardRestController#create} endpoint correctly responds with a {@link HttpServletResponse#SC_BAD_REQUEST}
     * and the JSON representation of a {@link ErrorSummary} when a request to create a null object is made.
     * {@link CardRestController#create} should respond with {@link HttpServletResponse#SC_UNSUPPORTED_MEDIA_TYPE}
     * because the request mapping for {@link CardRestController#create} will not be invoked because of the null value.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void createCardUsingNull() throws Exception {
        Card nullCard = null;

        // send a null body to CardRestController create endpoint
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(nullCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController create endpoint is working correctly
        ExtractableResponse<Response> createResponse = given()
                .when()
                .spec(requestSpecification)
                .post(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_BAD_REQUEST).extract();

        // map the endpoint response to an ErrorSummary object
        ErrorSummary errorSummary = mapper.readValue(createResponse.response().body().asString(), ErrorSummary.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(errorSummary);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, errorSummary.getStatus().intValue());
        assertNotNull(errorSummary.getMessage());
        assertNotNull(errorSummary.getError());
        assertNotNull(errorSummary.getTimestamp());
        assertNotNull(errorSummary.getMessage());
    }

    /**
     * Verify that {@link CardRestController#create} correctly responds with {@link HttpServletResponse#SC_PRECONDITION_FAILED}
     * when a request to create a {@link Card} with a non-null {@link Card#id} value is made.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void createCardNonNullId() throws Exception {
        // generate a test card value with an id already defined
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setId(new Random().longs(1L, Long.MAX_VALUE).findAny().getAsLong());

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(createCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController create endpoint is working correctly
        given()
            .when()
            .spec(requestSpecification)
            .post(RequestMappingConstants.Service.CARD)
            .then()
            .statusCode(HttpServletResponse.SC_PRECONDITION_FAILED).extract();
    }

    /**
     * Verify that {@link CardRestController#create} is working correctly when a request for a {@link Card} that
     * contains a value which exceeds the {@link CardDao#create} database configuration is made. The expectation
     * is that a JSON representation of a {@link ErrorSummary} object will be created and returned from within the
     * {@link ServiceErrorHandler} error handler.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void createCardColumnTooLong() throws Exception {
        // generate a test card value with a column that exceeds the database configuration
        Card createCard = TestUtils.cardWithTestValues();
        createCard.setCardName(RandomStringUtils.randomAlphabetic(2000));

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(createCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController create endpoint is working correctly
        ExtractableResponse<Response> createResponse = given()
                .when()
                .spec(requestSpecification)
                .post(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).extract();

        // map the endpoint response to an ErrorSummary object
        ErrorSummary errorSummary = mapper.readValue(createResponse.response().body().asString(), ErrorSummary.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(errorSummary);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorSummary.getStatus().intValue());
        assertNotNull(errorSummary.getMessage());
        assertNotNull(errorSummary.getError());
        assertNotNull(errorSummary.getTimestamp());
        assertNotNull(errorSummary.getMessage());
    }

    /**
     * The following test verifies that the {@link CardRestController#read} endpoint is functioning correctly.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void readCard() throws Exception {
        // ensure that CardDao create is working correctly
        Card testCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(testCard);
        assertNotNull(id);
        assertEquals(id, testCard.getId());

        // ensure that the CardRestController read endpoint is working correctly
        ExtractableResponse<Response> readResponse = given()
                .when()
                .get(RequestMappingConstants.Service.CARD + "/" + testCard.getId())
                .then()
                .statusCode(HttpServletResponse.SC_OK).extract();

        // map the endpoint response to a Card object
        Card cardResponse = mapper.readValue(readResponse.response().body().asString(), Card.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(cardResponse);
        assertEquals(testCard.getId(), cardResponse.getId());
        assertEquals(testCard, cardResponse);
    }

    /**
     * Verify that {@link CardRestController#read} endpoint correctly respond with {@link HttpServletResponse#SC_NOT_FOUND}
     * when a request for a non-existent {@link Card#id} is made.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void readCardNonExistent() throws Exception {
        // create a random card id that will not be in our local database
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();

        // ensure that the CardRestController read endpoint is working correctly
        given()
            .when()
            .get(RequestMappingConstants.Service.CARD + "/" + id)
            .then()
            .statusCode(HttpServletResponse.SC_NOT_FOUND).extract();
    }

    /**
     * The following test verifies that {@link CardDao#update} is working correctly and that the
     * {@link CardRestController#update} endpoint is also functioning correctly.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
    public void updateCard() throws Exception {
        // create a test card via the DAO
        Card testCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(testCard);
        assertNotNull(id);
        assertEquals(id, testCard.getId());

        // ensure that the CardRestController update endpoint is working correctly by sending it proper JSON
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(id);

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(updateCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        ExtractableResponse<Response> updateResponse = given()
                .when()
                .spec(requestSpecification)
                .put(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_OK).extract();

        // map the endpoint response to a card object
        Card cardResponse = mapper.readValue(updateResponse.response().body().asString(), Card.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(cardResponse);
        assertEquals(updateCard.getId(), cardResponse.getId());
        assertEquals(updateCard, cardResponse);

        // use the DAO to verify that the update performed via the endpoint above was successful
        Card readCard = cardDao.read(id);
        assertNotNull(readCard);
        assertEquals(updateCard, readCard);
    }
    /**
     * Verify that {@link CardRestController#update} is working correctly when a request for a {@link Card} that
     * contains a value which exceeds the {@link CardDao#update} database configuration is made. The expectation
     * is that a JSON representation of a {@link ErrorSummary} object will be updated and returned from within the
     * {@link ServiceErrorHandler} error handler.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
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

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(updateCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController update endpoint is working correctly
        ExtractableResponse<Response> updateResponse = given()
                .when()
                .spec(requestSpecification)
                .put(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).extract();

        // map the endpoint response to an ErrorSummary object
        ErrorSummary errorSummary = mapper.readValue(updateResponse.response().body().asString(), ErrorSummary.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(errorSummary);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorSummary.getStatus().intValue());
        assertNotNull(errorSummary.getMessage());
        assertNotNull(errorSummary.getError());
        assertNotNull(errorSummary.getTimestamp());
        assertNotNull(errorSummary.getMessage());
    }

    /**
     * Verify that {@link CardRestController#update} endpoint correctly responds with the JSON representation of a
     * {@link ErrorSummary} when a request to update a null object is made.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
    public void updateCardNull() throws Exception {
        Card nullCard = null;

        // send a null body to CardRestController update endpoint
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(nullCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController update endpoint is working correctly
        ExtractableResponse<Response> updateResponse = given()
                .when()
                .spec(requestSpecification)
                .put(RequestMappingConstants.Service.CARD)
                .then()
                .statusCode(HttpServletResponse.SC_BAD_REQUEST).extract();

        // map the endpoint response to an ErrorSummary object
        ErrorSummary errorSummary = mapper.readValue(updateResponse.response().body().asString(), ErrorSummary.class);

        // verify that the endpoint performed as expected by comparing input and output values
        assertNotNull(errorSummary);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, errorSummary.getStatus().intValue());
        assertNotNull(errorSummary.getMessage());
        assertNotNull(errorSummary.getError());
        assertNotNull(errorSummary.getTimestamp());
        assertNotNull(errorSummary.getMessage());
    }

    /**
     * Verify that {@link CardRestController#update} correctly responds with {@link HttpServletResponse#SC_PRECONDITION_FAILED}
     * when a request to update a {@link Card} with a null {@link Card#id} value is made.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
    public void updateCardNullId() throws Exception {
        // generate a test card value with an id already defined
        Card updateCard = TestUtils.cardWithTestValues();
        updateCard.setId(null);

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(mapper.writeValueAsString(updateCard));
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpecification = builder.build();

        // ensure that the CardRestController update endpoint is working correctly
        given()
            .when()
            .spec(requestSpecification)
            .put(RequestMappingConstants.Service.CARD)
            .then()
            .statusCode(HttpServletResponse.SC_PRECONDITION_FAILED).extract();
    }

    /**
     * The following test verifies that the {@link CardRestController#delete} endpoint is
     * functioning correctly.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
    public void deleteCard() throws Exception {
        // ensure that CardDao create is working correctly
        Card createCard = TestUtils.cardWithTestValues();
        Long id = cardDao.create(createCard);
        assertNotNull(id);
        assertEquals(id, createCard.getId());

        // verify that the card was created
        Card verifyCreateCard = cardDao.read(id);
        assertEquals(createCard, verifyCreateCard);

        // ensure that the CardRestController delete endpoint is working correctly
        given()
            .when()
            .delete(RequestMappingConstants.Service.CARD + "/" + createCard.getId())
            .then()
            .statusCode(HttpServletResponse.SC_OK).extract();

        // verify that the card was deleted
        assertNull(cardDao.read(id));
    }

    /**
     * Verify that {@link CardRestController#delete} correctly responds with {@link HttpServletResponse#SC_NOT_FOUND}
     * when a request for a non-existent {@link Card#id} is made.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Ignore
    @Test
    public void deleteCardNonExistent() throws Exception {
        // create a random card id that will not be in our local database
        Long id = new Random().longs(10000L, Long.MAX_VALUE).findAny().getAsLong();

        // ensure that the CardRestController delete endpoint is performing as expected
        given()
            .when()
            .delete(RequestMappingConstants.Service.CARD + "/" + id)
            .then()
            .statusCode(HttpServletResponse.SC_NOT_FOUND).extract();
    }

}
