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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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

    Body leftBorder;
    Body rightBorder;

    private double stickPreviousDirection;
    private double stickCurrentDirection;
//AndroidControls
   private boolean enableAndroidControls;

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

        enableAndroidControls = true;

        stick = new AnalogStick(game.batch);

        createBorders();
    }

    void createBorders(){
        BodyDef leftBorderBody = new BodyDef();
        leftBorderBody.position.set(0f, 5.9f); // x, y of body center
        leftBorderBody.type = BodyDef.BodyType.StaticBody;
        leftBorder = WorldCreator.world.createBody(leftBorderBody);

        BodyDef rightBorderBody = new BodyDef();
        rightBorderBody.position.set(19.18f, 5.88f); // x, y of body center
        rightBorderBody.type = BodyDef.BodyType.StaticBody;
        rightBorder = WorldCreator.world.createBody(rightBorderBody);

        PolygonShape border_shape = new PolygonShape();
        border_shape.setAsBox(1/StreetFighter.PPM, 5.9f/StreetFighter.PPM);

        FixtureDef leftBorderFixtureDef = new FixtureDef();
        leftBorderFixtureDef.shape = border_shape;
        leftBorder.createFixture(leftBorderFixtureDef);

        FixtureDef rightBorderFixtureDef = new FixtureDef();
        rightBorderFixtureDef.shape = border_shape;
        rightBorder.createFixture(rightBorderFixtureDef);
    }


    public void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.crouching == false) {
            player.jumping = true;
            player.player_body.setLinearVelocity(0, 12);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.crouching == false) {
            player.player_body.setLinearVelocity(6f, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.crouching == false) {
            player.player_body.setLinearVelocity(-5f, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.crouching = true;
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            System.out.println("mouse position.x " + (Gdx.input.getX()));
            System.out.println("mouse position.y " + (1080 - Gdx.input.getY()));
        }
        else {
            player.crouching = false;
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        }



        if(enableAndroidControls) {

//   8 inputs
//   down = -2 , down-left = -3, left = -4 and 4, left-up = 3, up = 2, right-up = 1, right = 0, right-down = -1

        float dx = stick.getKnobPercentX();
        float dy = stick.getKnobPercentY();

        double direction =  Math.floor((Math.atan2(dy, dx) + Math.PI/8) / (2*Math.PI/8));

        if (direction >= 8) direction = 0;
        double angle = direction * (Math.PI/4);


        stickCurrentDirection = direction;

            if (direction == -2.0) {
                player.crouching = true;
            }
            if ((direction == -3.0 || direction == 3.0 || direction == -4.0 || direction == 4.0) && player.crouching == false) {
                player.player_body.setLinearVelocity(-5f, 0);
            }
            if ((stick.isTouched() & (direction == 0.0 || direction == 1 || direction == -1) & stick.getKnobPercentX() != 0) && player.crouching == false) {
                player.player_body.setLinearVelocity(6f, 0);
            }
            if (direction == 2.0 & player.player_body.getPosition().y <= 6) {
                player.jumping = true;
                player.player_body.setLinearVelocity(0, 15);
            }
            if (direction != 2.0 & player.player_body.getPosition().y >= 6) {
                player.jumping = false;
                player.player_body.setLinearVelocity(0, -15);
            }

            if((direction == 3.0 || direction == -4 || direction == 4) & player.jumping == true & player.crouching == false)
                player.player_body.setLinearVelocity(-8f, 0);
            if((direction == 1 || direction == 0) & player.jumping == true & player.crouching == false)
                player.player_body.setLinearVelocity(8f, 0);

            if (!stick.isTouched()) {
                player.crouching = false;
                player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
            }



            stickPreviousDirection = stickCurrentDirection;
            if (player.jumping == true){
                System.out.println("jumping");
            }

// 4 inputs
// down = -1, left = -2 and 2, up = 1, right = 0;

//            float dx = stick.getKnobPercentX();
//            float dy = stick.getKnobPercentY();

//            double direction = Math.floor((Math.atan2(dy, dx) + Math.PI / 4) / (2 * Math.PI / 4));

//            if (direction >= 4) direction = 0;
//            double angle = direction * (Math.PI / 4);

//            if (direction == -1.0) {
//                player.crouching = true;
//            }
//            if ((direction == -2.0 || direction == 2.0) && player.crouching == false) {
//                player.player_body.setLinearVelocity(-5f, 0);
//            }
//            if (direction == 1.0) {
//                player.jumping = true;
//            }
//            if ((stick.isTouched() & direction == 0.0 & stick.getKnobPercentX() != 0) && player.crouching == false) {
//                player.player_body.setLinearVelocity(6f, 0);
//            }
//            if (!stick.isTouched()) {
//                player.crouching = false;
//                player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
//            }

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

        //hud.stage.draw();

        Gdx.input.setInputProcessor(stick.stage);
        stick.stage.act();
        stick.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

        printResolution(width, height);
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
          stick.dispose();
          player.dispose();
          manager.dispose();
    }

}

