package com.learnvest.qacodechallenge.service.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.learnvest.qacodechallenge.commons.db.BaseRowMapper;
import com.learnvest.qacodechallenge.commons.model.card.Card;

public class CardDaoRowMapper extends BaseRowMapper<Card> {

    @Override
    public Map<String, Object> mapObject(Card card) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", card.getId());
        map.put("card_name", card.getCardName());
        map.put("card_number", card.getCardNumber());
        map.put("card_image", card.getCardImage());
        map.put("card_image_mime_type", card.getCardImageMimeType());
        map.put("card_type", card.getCardType());
        map.put("card_description", card.getCardDescription());
        return map;
    }

    @Override
    public Card mapRow(ResultSet resultSet, int i) throws SQLException {
        Card card = new Card();
        card.setId(resultSet.getLong("id"));
        card.setCardName(resultSet.getString("card_name"));
        card.setCardNumber(resultSet.getString("card_number"));
        card.setCardImage(resultSet.getBytes("card_image"));
        card.setCardImageMimeType(resultSet.getString("card_image_mime_type"));
        card.setCardType(resultSet.getString("card_type"));
        card.setCardDescription(resultSet.getString("card_description"));
        return card;
    }

}
