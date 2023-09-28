package com.ismaelrh.gameboy.input;

public class InputState {

    //Action
    private boolean select = false;
    private boolean start = false;
    private boolean b = false;
    private boolean a = false;

    //Direction
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;

    private ButtonPressedCallback callback;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
        doCallback(select);
    }

    public boolean startPressed() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
        doCallback(start);
    }

    public boolean bPressed() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
        doCallback(b);
    }

    public boolean aPressed() {
        return a;
    }

    public void setA(boolean a) {
        this.a = a;
        doCallback(a);
    }

    public boolean upPressed() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
        doCallback(up);
    }

    public boolean downPressed() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
        doCallback(down);
    }

    public boolean leftPressed() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
        doCallback(left);
    }

    public boolean rightPressed() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
        doCallback(right);
    }

    public void registerCallback(ButtonPressedCallback callback) {
        this.callback = callback;
    }

    private void doCallback(boolean pressed) {
        if (pressed) {
            callback.notifyButtonPressed();
        }
    }

}
