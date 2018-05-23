package com.learnvest.qacodechallenge.commons.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.net.MediaType;
import com.learnvest.qacodechallenge.commons.model.card.Card;

public class TestUtils {

    //Check:OFF: MagicNumber
    public static Card cardWithTestValues() {
        Card card = new Card();
        card.setCardName(CharMatcher.JAVA_ISO_CONTROL.removeFrom(RandomStringUtils.random(75)));
        card.setCardNumber(RandomStringUtils.randomNumeric(25));
        card.setCardImage(getRandomByteArray());
        card.setCardImageMimeType(getRandomImageMimeType());
        card.setCardType(RandomStringUtils.randomAlphanumeric(20));
        return card;
    }

    public static byte[] getRandomByteArray() {
        int max = 100;
        int min = 20;
        Random random = new Random();
        int randomByteCount = random.nextInt(max - min + 1) + min;
        byte[] randomBytes = new byte[randomByteCount];
        random.nextBytes(randomBytes);
        return randomBytes;
    }
    //Check:ON: MagicNumber

    public static String getRandomImageMimeType() {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MediaType.GIF.toString());
        mimeTypes.add(MediaType.PNG.toString());
        mimeTypes.add(MediaType.JPEG.toString());
        return getRandomStringFromList(mimeTypes);
    }

    public static String getRandomStringFromList(List<String> strings) {
        String value = null;
        if (strings != null && !strings.isEmpty()) {
            int index = new Random().nextInt(strings.size());
            value = strings.get(index);
        }
        return value;
    }

}
