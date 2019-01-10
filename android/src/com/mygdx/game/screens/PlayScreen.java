package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.scenes.HUD;

public class PlayScreen implements Screen {

    private StreetFighter game;
    Texture texture;
    private OrthographicCamera camera;
    private Viewport viewport;
    private HUD hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;


    public PlayScreen(StreetFighter game){
        this.game = game;
        texture = new Texture("frame_00_delay-0.06s.gif");
        camera = new OrthographicCamera();
        viewport = new StretchViewport(800, 480, camera);
        hud = new HUD(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
    }

    public void handleInput(float dt){
        if(Gdx.input.isTouched()){
            camera.position.x += 50 + dt;
        }
    }

    public void update(float dt){
     handleInput(dt);

     camera.update();
     renderer.setView(camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

      Gdx.gl.glClearColor(0,0,0,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      renderer.render();

      game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
      hud.stage.draw();
      /*
      game.batch.setProjectionMatrix(camera.combined);
      game.batch.begin();
      game.batch.draw(texture, 0,0);
      game.batch.end();
      */
    }

    @Override
    public void resize(int width, int height) {
         viewport.update(width, height);
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
