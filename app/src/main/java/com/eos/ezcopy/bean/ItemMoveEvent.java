package com.eos.ezcopy.bean;

public class ItemMoveEvent {

    int srcPosition;
    int dstPosition;

    public ItemMoveEvent(int srcPosition, int dstPosition) {
        this.srcPosition = srcPosition;
        this.dstPosition = dstPosition;
    }

    public int getSrcPosition() {
        return srcPosition;
    }

    public void setSrcPosition(int srcPosition) {
        this.srcPosition = srcPosition;
    }

    public int getDstPosition() {
        return dstPosition;
    }

    public void setDstPosition(int dstPosition) {
        this.dstPosition = dstPosition;
    }
}
