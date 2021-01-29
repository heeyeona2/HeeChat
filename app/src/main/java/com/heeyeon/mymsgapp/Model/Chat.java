package com.heeyeon.mymsgapp.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private int effect;
    private int backeffect;

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getBackeffect() {
        return backeffect;
    }

    public void setBackeffect(int backeffect) {
        this.backeffect = backeffect;
    }

    public int getBoxeffect() {
        return boxeffect;
    }

    public void setBoxeffect(int boxeffect) {
        this.boxeffect = boxeffect;
    }

    private int boxeffect;


    public Chat(String sender, String receiver, String message, boolean isseen, int effect, int backeffect, int boxeffect) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.effect = effect;
        this.backeffect = backeffect;
        this.boxeffect = boxeffect;
    }

    public Chat(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
