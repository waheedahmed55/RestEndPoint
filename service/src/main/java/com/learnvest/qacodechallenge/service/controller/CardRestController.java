package com.learnvest.qacodechallenge.service.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.learnvest.qacodechallenge.commons.constants.RequestMappingConstants;
import com.learnvest.qacodechallenge.commons.model.card.Card;
import com.learnvest.qacodechallenge.service.db.CardDao;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(RequestMappingConstants.Service.CARD)
public class CardRestController {

    @Autowired
    CardDao cardDao;

    /**
     * Create the provided {@link Card}.
     * @param card {@link Card}
     * @param response {@link javax.servlet.http.HttpServletResponse}
     * @return {@link Card}
     */
    @ApiOperation(value = "Create a card")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Card create(@RequestBody Card card, @ApiIgnore HttpServletResponse response) {
        if (card.getId() != null) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }
        cardDao.create(card);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return card;
    }

    /**
     * Retrieve the {@link Card} based on the provided {@link Card#id}.
     * @param cardId long value of {@link Card#id}
     * @param response {@link javax.servlet.http.HttpServletResponse}
     * @return {@link Card}
     */
    @ApiOperation(value = "Retrieve a specific card by its id")
    @RequestMapping(value = "/{cardId}", method = RequestMethod.GET)
    public Card read(@PathVariable long cardId, @ApiIgnore HttpServletResponse response) {
        Card card = cardDao.read(cardId);
        if (card == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return card;
    }

    /**
     * Update the provided {@link Card}.
     * @param card {@link Card}
     * @param response {@link javax.servlet.http.HttpServletResponse}
     * @return {@link Card}
     */
    @ApiOperation(value = "Update a card")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Card update(@RequestBody Card card, @ApiIgnore HttpServletResponse response) {
        if (card.getId() == null) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }
        cardDao.update(card);
        return card;
    }

    /**
     * Delete the {@link Card} referred to by the provided {@link Card#id}.
     * @param cardId long value of {@link Card#id}
     * @param response {@link javax.servlet.http.HttpServletResponse}
     */
    @ApiOperation(value = "Delete a specific card by its id")
    @RequestMapping(value = "/{cardId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable long cardId, @ApiIgnore HttpServletResponse response) {

        // TODO: If the requested {@link Card} is not found
        // TODO: then return {@link HttpServletResponse} status of {@link HttpServletResponse#SC_NOT_FOUND}

    }

}
