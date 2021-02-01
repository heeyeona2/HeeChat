package com.heeyeon.mymsgapp.Model;

public class Card {
    boolean flag;
    int Image;
    boolean LongPressed;

    public boolean getLongPressed() {
        return LongPressed;
    }

    public void setLongPressed(boolean longPressed) {
        LongPressed = longPressed;
    }


    public Card(boolean flag, int image) {
        this.flag = flag;
        this.Image = image;
        this.LongPressed =false;
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
