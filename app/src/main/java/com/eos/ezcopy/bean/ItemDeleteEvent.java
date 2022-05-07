package com.eos.ezcopy.bean;

public class ItemDeleteEvent {

    int position;

    public ItemDeleteEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
