package com.learnvest.qacodechallenge.integration.requestor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnvest.qacodechallenge.commons.constants.RequestMappingConstants;
import com.learnvest.qacodechallenge.commons.model.card.Card;

@Service
@EnableRetry
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class CardRequestor extends BaseRequestor {

    private static final Logger LOG = LoggerFactory.getLogger(CardRequestor.class);

    private RestTemplate restTemplate;
    private String cardUrl;

    public CardRequestor(String serviceHost, RestTemplate restTemplate) {
        this.serviceHost = serviceHost;
        this.restTemplate = restTemplate;
        this.cardUrl = serviceHost + RequestMappingConstants.Service.CARD;
    }

    /**
     * Perform the create {@link Card} request by executing a {@link HttpMethod#POST} against the
     * create endpoint mapped at {@link RequestMappingConstants.Service#CARD}.
     * @param httpHeaders {@link HttpHeaders}
     * @param card {@link Card}
     * @return {@link Card}
     * @throws Exception caught is rethrown
     */
    @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_BACKOFF_DELAY))
    public Card create(HttpHeaders httpHeaders, Card card) throws Exception {
        HttpEntity<Card> httpEntity = new HttpEntity<>(card, httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cardUrl);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    builder.build().encode().toUri(), HttpMethod.POST, httpEntity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseEntity.getBody(), Card.class);
        } catch (Exception e) {
            LOG.error("Unable to create card: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Perform the read {@link Card} request by executing a {@link HttpMethod#GET} against the
     * read endpoint mapped at {@link RequestMappingConstants.Service#CARD}.
     * @param httpHeaders {@link HttpHeaders}
     * @param cardId {@link Card#id}
     * @return {@link Card}
     * @throws Exception caught is rethrown
     */
    @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_BACKOFF_DELAY))
    public Card read(HttpHeaders httpHeaders, long cardId) throws Exception {
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cardUrl + "/" + cardId);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(), HttpMethod.GET, httpEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseEntity.getBody(), Card.class);
    }

    /**
     * Perform the update {@link Card} request by executing a {@link HttpMethod#PUT} against the
     * update endpoint mapped at {@link RequestMappingConstants.Service#CARD}.
     * @param httpHeaders {@link HttpHeaders}
     * @param card {@link Card}
     * @return {@link Card}
     * @throws Exception caught is rethrown
     */
    public Card update(HttpHeaders httpHeaders, Card card) throws Exception {
        HttpEntity<Card> httpEntity = new HttpEntity<>(card, httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cardUrl);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(), HttpMethod.PUT, httpEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseEntity.getBody(), Card.class);
    }

    /**
     * Perform the delete {@link Card} request by executing a {@link HttpMethod#DELETE} against the
     * delete endpoint mapped at {@link RequestMappingConstants.Service#CARD}.
     * @param httpHeaders {@link HttpHeaders}
     * @param cardId {@link Card#id}
     * @throws Exception caught is rethrown
     */
    public HttpStatus delete(HttpHeaders httpHeaders, long cardId) throws Exception {
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(cardUrl + "/" + cardId);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(), HttpMethod.DELETE, httpEntity, String.class);
        return responseEntity.getStatusCode();
    }

}
