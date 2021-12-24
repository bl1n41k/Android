package com.example.voiceassistent.messageview;

import java.util.Date;

public class Message {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend){
        this.text = text;
        this.date = new Date();
        this.isSend = isSend;
    }
    public Message(MessageEntity entity) {
        this.text = entity.text;
        if (entity.date == null)
            this.date = null;
        else
            this.date = new Date(entity.date);
        this.isSend = entity.isSend != 0;
    }

}
