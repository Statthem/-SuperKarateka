package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.StreetFighter;

public class PlayScreen implements Screen {

    private StreetFighter game;
    Texture texture;

    public PlayScreen(StreetFighter game){
        this.game = game;
        texture = new Texture("ryustance.gif");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
      Gdx.gl.glClearColor(1,0,0,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      game.batch.begin();
      game.batch.draw(texture, 0,0);
      game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
