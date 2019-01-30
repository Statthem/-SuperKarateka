package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.controls.AnalogStick;
import com.mygdx.game.scenes.HUD;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.world.MyWorld;
import com.mygdx.game.sprites.Ryu;

import static com.mygdx.game.world.MyWorld.mapRenderer;
import static com.mygdx.game.world.MyWorld.b2dr;
import static com.mygdx.game.world.MyWorld.world;
import static com.mygdx.game.world.MyWorld.map;

public class PlayScreen implements Screen {

    private StreetFighter game;
    private OrthographicCamera camera;

    //Viewport variables
    private Viewport viewport;

    Player player;
    Player player2;

    private HUD hud;
    AnalogStick stick;

    private double stickPreviousDirection;
    private double stickCurrentDirection;

   private boolean enableAndroidControls;

    public AssetManager manager;

    public PlayScreen(StreetFighter game){
        this.game = game;

        camera = new OrthographicCamera();

        //camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        camera.position.set((StreetFighter.resolution_width/2)/StreetFighter.PPM, (StreetFighter.resolution_height/2)/StreetFighter.PPM, 0);

        viewport = new FillViewport(StreetFighter.resolution_width/StreetFighter.PPM, StreetFighter.resolution_height/StreetFighter.PPM, camera);

        MyWorld myWorld = new MyWorld();

        manager = new AssetManager();

        player = new Ryu(this, true, myWorld);
        player2 = new Ryu(this, false, myWorld);

        hud = new HUD(game.batch);

        enableAndroidControls = true;
        stick = new AnalogStick(game.batch);
    }




    public void handleInput(float dt) {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.crouching == false) {
//            player.jumping = true;
//            player.player_body.setLinearVelocity(0, 12);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.crouching == false) {
//            player.player_body.setLinearVelocity(6f, 0);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.crouching == false) {
//            player.player_body.setLinearVelocity(-5f, 0);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//             player.crouching = true;
//             player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
//            System.out.println("mouse position.x " + (Gdx.input.getX()));
//            System.out.println("mouse position.y " + (1080 - Gdx.input.getY()));
//        }
//        else {
//            player.crouching = false;
//            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
//        }



        if(enableAndroidControls) {

          //  player.crouching = false;
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);

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
            if (direction != -2.0){
                player.crouching = false;
            }


            if (player.crouching == true) {
                if ((player.getPreviousState() == Player.State.STANDING
                        || player.getPreviousState() == Player.State.MOVING_RIGHT
                        || player.getPreviousState() == Player.State.MOVING_LEFT
                        || player.getPreviousState() == Player.State.CROUCHING1)) {
                    player.setCurrentState(Player.State.CROUCHING1);
                }
                if ((player.getPreviousState() == Player.State.CROUCHING1
                        & player.getCurrentState() == Player.State.CROUCHING1
                        & player.isAnimationFinished(player.crouchingAnimation1))
                        || player.getPreviousState() == Player.State.CROUCHING2){
                    player.setCurrentState(Player.State.CROUCHING2);
                }
            }
            if(player.crouching == false ){
                if(player.getPreviousState() == Player.State.CROUCHING1
                        & !player.isAnimationFinished(player.crouchingAnimation1)) {
                    player.setCurrentState(Player.State.CROUCHING1);
                }
                if((player.getPreviousState() == Player.State.CROUCHING1
                        & player.isAnimationFinished(player.crouchingAnimation1))
                        ||  player.getPreviousState() == Player.State.CROUCHING2
                        || player.getPreviousState() == Player.State.CROUCHING3) {
                    player.setCurrentState(Player.State.CROUCHING3);
                }
            }

            if ((direction == -3.0 || direction == 3.0 || direction == -4.0 || direction == 4.0)
                    & !player.getPreviousState().equals(Player.State.CROUCHING3)
                    & !player.getPreviousState().equals(Player.State.CROUCHING2)
                    & !player.getPreviousState().equals(Player.State.CROUCHING1)
                    & !player.getPreviousState().equals(Player.State.JUMPINGRIGHT)
                    & !player.getPreviousState().equals(Player.State.JUMPINGLEFT)
                    & !player.getPreviousState().equals(Player.State.JUMPING)) {
                player.setCurrentState(Player.State.MOVING_LEFT);
            }
            if ((stick.isTouched() & (direction == 0.0 || direction == 1 || direction == -1) & stick.getKnobPercentX() != 0)  & !player.getPreviousState().equals(Player.State.CROUCHING3)
                    & !player.getPreviousState().equals(Player.State.CROUCHING2)
                    & !player.getPreviousState().equals(Player.State.CROUCHING1)
                    & !player.getPreviousState().equals(Player.State.JUMPINGRIGHT)
                    & !player.getPreviousState().equals(Player.State.JUMPINGLEFT)
                    & !player.getPreviousState().equals(Player.State.JUMPING)) {
                player.setCurrentState(Player.State.MOVING_RIGHT);
            }

            //jumping
            if (direction == 2.0 & (stickPreviousDirection != 2.0 & stickPreviousDirection != 3.0 & stickPreviousDirection != 1)
                    & (player.getCurrentState().equals(Player.State.STANDING)
                    || (player.getPreviousState().equals(Player.State.CROUCHING3) & player.isAnimationFinished(player.crouchingAnimation3)))) {
                player.setCurrentState(Player.State.JUMPING);
            }

            //jumping left and right
            if(stickPreviousDirection == 2 & direction == 3
                    & (player.getPreviousState().equals(Player.State.STANDING)
                    || player.getCurrentState().equals(Player.State.MOVING_RIGHT)
                    || player.getCurrentState().equals(Player.State.MOVING_LEFT))){
            player.setCurrentState(Player.State.JUMPINGLEFT);
            }
            if(stickPreviousDirection == 2 & direction == 1
                    & (player.getPreviousState().equals(Player.State.STANDING)
                    || player.getCurrentState().equals(Player.State.MOVING_RIGHT)
                    || player.getCurrentState().equals(Player.State.MOVING_LEFT))){
                player.setCurrentState(Player.State.JUMPINGRIGHT);
            }


            if(player.getPreviousState().equals(Player.State.JUMPING) & player.isAnimationFinished(player.jumpingAnimation)
                    || player.getPreviousState().equals(Player.State.JUMPINGLEFT) & player.isAnimationFinished(player.jumpingLeftAnimation)
                    || player.getPreviousState().equals(Player.State.JUMPINGRIGHT) & player.isAnimationFinished(player.jumpingRightAnimation)) {
                player.setCurrentState(Player.State.CROUCHING3);
            }

            if (!stick.isTouched()
                    & !player.getCurrentState().equals(Player.State.JUMPING)
                    & !player.getCurrentState().equals(Player.State.JUMPINGLEFT)
                    & !player.getCurrentState().equals(Player.State.JUMPINGRIGHT)
                    & !player.getCurrentState().equals(Player.State.CROUCHING1)
                    & !player.getCurrentState().equals(Player.State.CROUCHING3)){
                player.setCurrentState(Player.State.STANDING);
            }
            if(stick.isTouched()
                    & stick.getKnobPercentX() == 0
                    & !player.getCurrentState().equals(Player.State.JUMPING)
                    & !player.getCurrentState().equals(Player.State.JUMPINGLEFT)
                    & !player.getCurrentState().equals(Player.State.JUMPINGRIGHT)
                    & !player.getCurrentState().equals(Player.State.CROUCHING1)
                    & !player.getCurrentState().equals(Player.State.CROUCHING3)){
                player.setCurrentState(Player.State.STANDING);
            }

            if(player.getPreviousState().equals(Player.State.CROUCHING3) & player.isAnimationFinished(player.crouchingAnimation3)) {
                player.setCurrentState(Player.State.STANDING);
            }

            //moving while jumping
//            if((direction == 3.0 || direction == -4 || direction == 4) & player.jumping == true & player.crouching == false) {
//                player.player_body.setLinearVelocity(-8f, player.player_body.getLinearVelocity().y);
//            }
//            if((direction == 1 || direction == 0) & player.jumping == true & player.crouching == false)
//                player.player_body.setLinearVelocity(8f, player.player_body.getLinearVelocity().y);

            stickPreviousDirection = stickCurrentDirection;

            setPlayerBodySpeedByState();

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

    public void setPlayerBodySpeedByState() {

        if (player.getCurrentState().equals(Player.State.MOVING_RIGHT)) {
            player.player_body.setLinearVelocity(6f, 0);
        } else if (player.getCurrentState().equals(Player.State.MOVING_LEFT)) {
            player.player_body.setLinearVelocity(-5f, 0);
        } else if (player.getCurrentState().equals(Player.State.STANDING)) {
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        } else if (player.getCurrentState().equals(Player.State.JUMPING) & player.player_body.getPosition().y <= 6.5 & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(0, 15);
        } else if (player.getCurrentState().equals(Player.State.JUMPING) & player.player_body.getPosition().y >= 6.5) {
            player.player_body.setLinearVelocity(0, -11);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getPosition().y <= 6.5 & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(-11, 11);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getPosition().y >= 6.5) {
            player.player_body.setLinearVelocity(0, -12);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getLinearVelocity().y < 0) {
            player.player_body.setLinearVelocity(-8, -16);
        }else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getPosition().y <= 6.5 & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(7, 11);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getPosition().y >= 6.5) {
            player.player_body.setLinearVelocity(0, -12);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getLinearVelocity().y < 0) {
            player.player_body.setLinearVelocity(7, -16);
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
     player2.update(dt);

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
            player2.draw(game.batch);
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

