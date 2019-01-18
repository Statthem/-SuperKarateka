package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.controls.AnalogStick;
import com.mygdx.game.scenes.HUD;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.creators.WorldCreator;
import com.mygdx.game.sprites.Ryu;

import static com.mygdx.game.creators.WorldCreator.mapRenderer;
import static com.mygdx.game.creators.WorldCreator.b2dr;
import static com.mygdx.game.creators.WorldCreator.world;
import static com.mygdx.game.creators.WorldCreator.map;

public class PlayScreen implements Screen {

    private StreetFighter game;
    private OrthographicCamera camera;

    //Viewport variables
    private Viewport viewport;
    private Viewport fitViewport;
    private Viewport fillViewport;

    Player player;

    private HUD hud;
    AnalogStick stick;

//AndroidControls
    Rectangle wleftBounds;
    Rectangle wrightBounds;
    Vector3 touchPoint;

    public AssetManager manager;

    public PlayScreen(StreetFighter game){
        this.game = game;

        //create camera
        camera = new OrthographicCamera();

        //camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        camera.position.set((StreetFighter.resolution_width/2)/StreetFighter.PPM, (StreetFighter.resolution_height/2)/StreetFighter.PPM, 0);

        //Viewport for maintaining aspect ratio
        viewport = new FillViewport(StreetFighter.resolution_width/StreetFighter.PPM, StreetFighter.resolution_height/StreetFighter.PPM, camera);
//        fitViewport = new FillViewport(800, 480, camera);

        WorldCreator streetFighterWorldCreator = new WorldCreator();
        streetFighterWorldCreator.createWorld();

        manager = new AssetManager();

        player = new Ryu(this, true);

        hud = new HUD(game.batch);

        stick = new AnalogStick(game.batch);

        createAndroidControls();

    }


    public void createAndroidControls(){
        wleftBounds = new Rectangle(0,0, 1000/100,1080/100);
        wrightBounds = new Rectangle(1000/100, 540/100, 1920/100, 1080/100);

        touchPoint = new Vector3();
    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.player_body.applyLinearImpulse(new Vector2(0, 0.5f), player.player_body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.player_body.setLinearVelocity(6f, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.player_body.setLinearVelocity(-5f, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.crouching = true;
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        } else {
            player.crouching = false;
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        }


// Android controls (undeveloped

/*
        float dx = stick.getKnobPercentX();
        float dy = stick.getKnobPercentY();

        System.out.println(dx  + " " + dy);

        if(dx > 0)  player.player_body.setLinearVelocity(6f,0);
        if(dx < 0)  player.player_body.setLinearVelocity(-5f,0);


*/
        //   Try this, took me like one hour to figure it out, so I hope it works for you!

// 4 inputs
// down = -1, left = -2 and 2, up = 1, right = 0;
        float dx = stick.getKnobPercentX();
        float dy = stick.getKnobPercentY();

        double direction = Math.floor((Math.atan2(dy, dx) + Math.PI / 4) / (2 * Math.PI / 4));

        if (direction >= 4) direction = 0;
        double angle = direction * (Math.PI / 4);


//   8 inputs
//   down = -2 , down-left = -3, left = -4 and 4, left-up = 3, up = 2, right-up = 1, right = 0, right-down = -1

//        float dx = stick.getKnobPercentX();
//        float dy = stick.getKnobPercentY();
//
//        double direction =  Math.floor((Math.atan2(dy, dx) + Math.PI/8) / (2*Math.PI/8));

//        if (direction >= 8) direction = 0;
//        double angle = direction * (Math.PI/4);


        if (direction == -1.0) {
            player.crouching = true;
        }
        if (direction == -2.0 || direction == 2.0) {
            player.player_body.setLinearVelocity(-5f, 0);
        }
        if (direction == 1.0) {
            player.crouching = false;
        }
        if (Gdx.input.isTouched() && direction == 0.0) {
            player.player_body.setLinearVelocity(6f, 0);
        }

    }



    /*
     * shows frame-rate deviation
     * 1 - fps == 60, > 1 - fps is lower than 60, < 1 fps is higher than 60
     */
    private void printFrameRateDeviation(float delta) {
        System.out.printf("FrameRateDeviation: " + "%.2f" + "\n", delta * 60);
    }

    private void printResolution(int width, int height){
        System.out.println("Resolution: " + width + " " + height);
    }

    public void update(float dt){
     handleInput(dt);

     world.step(1/60f,6,2);

        player.update(dt);

     //update our camera with correct coordinates after changes
     camera.update();

     //tell our renderer to draw only what our camera can see
     mapRenderer.setView(camera);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
       //  printFrameRateDeviation(Gdx.graphics.getDeltaTime());

        update(delta);

        //clear the game screen with black
      Gdx.gl.glClearColor(0,0,0,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      //render(draw tiled map)
      mapRenderer.render();

      //render Box2dDebugLines
      b2dr.render(world, camera.combined);

      game.batch.setProjectionMatrix(camera.combined);

            game.batch.begin();
            player.draw(game.batch);
            game.batch.end();


      //draw our game Hud
        //hud.stage.draw();

        Gdx.input.setInputProcessor(stick.stage);
        stick.stage.act();
        stick.stage.draw();

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

        //printResolution(width, height);
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
          map.dispose();
          mapRenderer.dispose();
          world.dispose();
          b2dr.dispose();
          hud.dispose();
          stick.stage.dispose();
          player.atlas.dispose();
          manager.unload("StreetFighter3_Resources/Sprites/Ryu/packs/Ryu_basic_pack.atlas");

    }

}

