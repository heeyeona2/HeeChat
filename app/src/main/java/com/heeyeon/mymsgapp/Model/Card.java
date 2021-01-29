package com.heeyeon.mymsgapp.Model;

public class Card {
    boolean flag;
    int Image;

    public Card(boolean flag, int image) {
        this.flag = flag;
        Image = image;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}
