package com.learnvest.qacodechallenge.integration.requestor;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnvest.qacodechallenge.commons.constants.RequestMappingConstants;
import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.commons.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ContextConfiguration(classes = CardRequestorUnitTest.Config.class)
@TestPropertySource(locations="classpath:test.properties")
public class CardRequestorUnitTest extends AbstractJUnit4SpringContextTests {

    public static final String hostUrl = "http://localhost:8080";

    @Configuration
    static class Config {

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        CardRequestor cardRequestor() {
            return new CardRequestor(hostUrl, restTemplate());
        }

    }

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CardRequestor cardRequestor;

    MockRestServiceServer mockServer;

    ObjectMapper mapper;

    @Before
    public void setUp() {
        assertNotNull(restTemplate);
        assertNotNull(cardRequestor);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mapper = new ObjectMapper();
    }

    /**
     * The following test verifies that {@link CardRequestor#create} endpoint is working correctly when a request to
     * the endpoint that is mapped as {@link RequestMappingConstants.Service#CARD} responds with the JSON provided.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void create() throws Exception {
        // ensure that CardRequestor create is working correctly when the following mocked JSON is received
        Long id = new Random().longs(5000L, Long.MAX_VALUE).findAny().getAsLong();
        Card card = TestUtils.cardWithTestValues();
        card.setId(id);
        String returnCardJson = mapper.writeValueAsString(card);

        mockServer.expect(
                requestTo(hostUrl + RequestMappingConstants.Service.CARD))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(returnCardJson, MediaType.APPLICATION_JSON_UTF8));

        // exercise our integration component and ensure it responds with the expected result
        card.setId(null);
        HttpHeaders httpHeaders = new HttpHeaders();
        Card cardRequestorCard = cardRequestor.create(httpHeaders, card);
        assertEquals(id, cardRequestorCard.getId());
        assertEquals(card, cardRequestorCard);
    }

    /**
     * The following test verifies that the {@link CardRequestor#read} endpoint is working correctly when a request to
     * the endpoint that is mapped as {@link RequestMappingConstants.Service#CARD} responds with the JSON provided.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void read() throws Exception {
        // ensure that CardRequestor read is working correctly when the following mocked JSON is received
        Long id = new Random().longs(5000L, Long.MAX_VALUE).findAny().getAsLong();
        Card card = TestUtils.cardWithTestValues();
        card.setId(id);
        assertEquals(id, card.getId());

        mockServer.expect(
                requestTo(hostUrl + RequestMappingConstants.Service.CARD + "/" + id))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(card), MediaType.APPLICATION_JSON_UTF8));

        // exercise our integration component and ensure it responds with the expected result
        HttpHeaders httpHeaders = new HttpHeaders();
        Card cardRequestorCard = cardRequestor.read(httpHeaders, id);
        assertEquals(id, cardRequestorCard.getId());
        assertEquals(card, cardRequestorCard);
    }

    /**
     * The following test verifies that the {@link CardRequestor#update} endpoint is working correctly when a request to
     * the endpoint that is mapped as {@link RequestMappingConstants.Service#CARD} responds with the JSON provided.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void update() throws Exception {
        // ensure that CardRequestor update is working correctly when the following mocked JSON is received
        Card card = TestUtils.cardWithTestValues();
        card.setId(new Random().longs(5000L, Long.MAX_VALUE).findAny().getAsLong());

        mockServer.expect(
                requestTo(hostUrl + RequestMappingConstants.Service.CARD))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(mapper.writeValueAsString(card), MediaType.APPLICATION_JSON_UTF8));

        // exercise our integration component and ensure it responds with the expected result
        HttpHeaders httpHeaders = new HttpHeaders();
        Card cardRequestorCard = cardRequestor.update(httpHeaders, card);
        assertEquals(card.getId(), cardRequestorCard.getId());
        assertEquals(card, cardRequestorCard);
    }

    /**
     * The following test verifies that the {@link CardRequestor#delete} endpoint is working correctly when a request to
     * the endpoint that is mapped as {@link RequestMappingConstants.Service#CARD} responds with the JSON provided.
     * @throws Exception within {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    @Test
    public void delete() throws Exception {

        Long id = new Random().longs(5000L, Long.MAX_VALUE).findAny().getAsLong();

        mockServer.expect(
                requestTo(hostUrl + RequestMappingConstants.Service.CARD + "/" + id))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpStatus httpStatus = cardRequestor.delete(httpHeaders, id);
        assertEquals(HttpStatus.OK, httpStatus);
    }

}
