package com.mygdx.game.extended;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyTextureRegion extends TextureRegion {

    private int width;
    private int height;
    private int framesAmount;

    public MyTextureRegion(TextureRegion region, int width, int height, int framesAmount){
        super(region);
        this.width = width;
        this.height = height;
        this.framesAmount = framesAmount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFramesAmount() {
        return framesAmount;
    }

    public void setFramesAmount(int framesAmount) {
        this.framesAmount = framesAmount;
    }
}
