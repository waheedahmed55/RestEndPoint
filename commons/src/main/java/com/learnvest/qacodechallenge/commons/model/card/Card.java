package com.learnvest.qacodechallenge.commons.model.card;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Card {

    private Long id;
    private String cardName;
    private String cardNumber;
    private byte[] cardImage;
    private String cardImageMimeType;
    private String cardType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public byte[] getCardImage() {
        return cardImage;
    }

    public void setCardImage(byte[] cardImage) {
        this.cardImage = cardImage;
    }

    public String getCardImageMimeType() {
        return cardImageMimeType;
    }

    public void setCardImageMimeType(String cardImageMimeType) {
        this.cardImageMimeType = cardImageMimeType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Card)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final Card otherObject = (Card) obj;

        return new EqualsBuilder()
                // intentionally ignoring id as this is the database key
                .append(this.cardName, otherObject.cardName)
                .append(this.cardNumber, otherObject.cardNumber)
                .append(this.cardImage, otherObject.cardImage)
                .append(this.cardImageMimeType, otherObject.cardImageMimeType)
                .append(this.cardType, otherObject.cardType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .append(this.cardName)
                .append(this.cardNumber)
                .append(this.cardImage)
                .append(this.cardImageMimeType)
                .append(this.cardType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
