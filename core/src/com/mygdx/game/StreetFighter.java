package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.PlayScreen;

public class StreetFighter extends Game {

    public static final float PPM = 100;
    public static final int resolution_width = 1920;
    public static final int resolution_height = 1080;
    public static final int svischuk_resolution_width = 1480;
    public static final int svischuk_resolution_height = 720;


    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
