package com.example.mycourseprojectapplication.Models;

import com.example.mycourseprojectapplication.Utilities.AESUtil;

import org.json.JSONObject;

import java.io.Serializable;

public class Card implements Serializable {

    private static final String JSON_CARD_NUMBER = "cardNumber";
    private static final String JSON_EXPIRED_DATE = "date";
    private static final String JSON_CARD_HOLDER = "holder";
    private static final String JSON_CCV_CODE = "ccv";


    private String cardNumber;
    private String expiredDate;
    private String cardHolder;
    private String cvvCode;

    public Card() {
    }

    public Card(String cardNumber, String expiredDate, String cardHolder, String cvvCode) {
        this.cardNumber = cardNumber;
        this.expiredDate = expiredDate;
        this.cardHolder = cardHolder;
        this.cvvCode = cvvCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    @Override
    public String toString() {
        return "Card info:\r\n" +
                "Card number = " + cardNumber + "\r\n" +
                "Expired date = " + expiredDate + "\r\n" +
                "Card holder = " + cardHolder + "\r\n" +
                "CVV code = " + cvvCode;
    }
    public JSONObject toJSON() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_CARD_NUMBER, AESUtil.encrypt(cardNumber));
        jsonObject.put(JSON_EXPIRED_DATE,AESUtil.encrypt(expiredDate));
        jsonObject.put(JSON_CARD_HOLDER,AESUtil.encrypt(cardHolder));
        jsonObject.put(JSON_CCV_CODE,AESUtil.encrypt(cvvCode));

        return jsonObject;
    }

    public Card(JSONObject jsonObject) throws Exception {
        cardNumber = AESUtil.decrypt(jsonObject.getString(JSON_CARD_NUMBER));
        expiredDate =  AESUtil.decrypt(jsonObject.getString(JSON_EXPIRED_DATE));
        cardHolder =  AESUtil.decrypt(jsonObject.getString(JSON_CARD_HOLDER));
        cvvCode =  AESUtil.decrypt(jsonObject.getString(JSON_CCV_CODE));
    }


}
