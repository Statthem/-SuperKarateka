package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.contact_listeners.PlayerContactListener;
import com.mygdx.game.controls.AnalogStick;
import com.mygdx.game.scenes.HUD;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.world.MyWorld;
import com.mygdx.game.sprites.Ryu;

import static  com.mygdx.game.sprites.Player.State;

import static com.mygdx.game.world.MyWorld.mapRenderer;
import static com.mygdx.game.world.MyWorld.b2dr;
import static com.mygdx.game.world.MyWorld.world;
import static com.mygdx.game.world.MyWorld.map;

public class PlayScreen implements Screen {

    private StreetFighter game;
    private OrthographicCamera camera;

    //Viewport variables
    private Viewport viewport;

    Player player1;
    Player player2;

    private HUD hud;
    AnalogStick stick;

    private double stickPreviousDirection;
    private double stickCurrentDirection;

   private boolean enableAndroidControls;
   private boolean overlaped;

    public AssetManager manager;

    public PlayScreen(StreetFighter game){
        this.game = game;

        camera = new OrthographicCamera();

        //camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        camera.position.set((StreetFighter.resolution_width/2)/StreetFighter.PPM, (StreetFighter.resolution_height/2)/StreetFighter.PPM, 0);

        viewport = new FillViewport(StreetFighter.resolution_width/StreetFighter.PPM, StreetFighter.resolution_height/StreetFighter.PPM, camera);

        MyWorld myWorld = new MyWorld();

        manager = new AssetManager();

        player1 = new Ryu(this, true, myWorld);
        player2 = new Ryu(this, false, myWorld);

        ContactListener contactListener = new PlayerContactListener(player1, player2, this);
        world.setContactListener(contactListener);

        hud = new HUD(game.batch);

        enableAndroidControls = true;
        stick = new AnalogStick(game.batch);
    }

    private void handleInput(float dt) {
             handlePlayer1Inputs(dt);
             handlePlayer2Inputs(dt);
    }

    private void handlePlayer1Inputs(float dt){
        if(enableAndroidControls) {
            player1.getPlayer_body().setLinearVelocity(0, player1.getPlayer_body().getLinearVelocity().y);

//   8 inputs
//   down = -2 , down-left = -3, left = -4 and 4, left-up = 3, up = 2, right-up = 1, right = 0, right-down = -1

            float dx = stick.getKnobPercentX();
            float dy = stick.getKnobPercentY();

            double direction =  Math.floor((Math.atan2(dy, dx) + Math.PI/8) / (2*Math.PI/8));

            if (direction >= 8) direction = 0;
            double angle = direction * (Math.PI/4);

            stickCurrentDirection = direction;


            if (direction == -2.0) {
                player1.crouching = true;
            }
            if (direction != -2.0){
                player1.crouching = false;
            }


            if (player1.crouching == true) {
                if ((player1.getPreviousState() == State.STANDING
                        || player1.getPreviousState() == State.MOVING_FORWARD
                        || player1.getPreviousState() == State.MOVING_BACK
                        || player1.getPreviousState() == State.CROUCHING1)) {
                    player1.setCurrentState(State.CROUCHING1);
                }
                if ((player1.getPreviousState() == State.CROUCHING1
                        & player1.getCurrentState() == State.CROUCHING1
                        & player1.isAnimationFinished(player1.crouchingAnimation1))
                        || player1.getPreviousState() == State.CROUCHING2){
                    player1.setCurrentState(State.CROUCHING2);
                }
            }
            if(player1.crouching == false ){
                if(player1.getPreviousState() == State.CROUCHING1
                        & !player1.isAnimationFinished(player1.crouchingAnimation1)) {
                    player1.setCurrentState(State.CROUCHING1);
                }
                if((player1.getPreviousState() == State.CROUCHING1
                        & player1.isAnimationFinished(player1.crouchingAnimation1))
                        ||  player1.getPreviousState() == State.CROUCHING2
                        || player1.getPreviousState() == State.CROUCHING3) {
                    player1.setCurrentState(State.CROUCHING3);
                }
            }

            if ((direction == -3.0 || direction == 3.0 || direction == -4.0 || direction == 4.0)
                    & !player1.getPreviousState().equals(State.CROUCHING3)
                    & !player1.getPreviousState().equals(State.CROUCHING2)
                    & !player1.getPreviousState().equals(State.CROUCHING1)
                    & !player1.getPreviousState().equals(State.JUMPING_FORWARD)
                    & !player1.getPreviousState().equals(State.JUMPING_BACK)
                    & !player1.getPreviousState().equals(State.JUMPING)) {
                player1.setCurrentState(State.MOVING_BACK);
                player1.setCurrentSpeed(player1.walkingBackSpeed);
            }
            if ((stick.isTouched() & (direction == 0.0 || direction == 1 || direction == -1) & stick.getKnobPercentX() != 0)  & !player1.getPreviousState().equals(State.CROUCHING3)
                    & !player1.getPreviousState().equals(State.CROUCHING2)
                    & !player1.getPreviousState().equals(State.CROUCHING1)
                    & !player1.getPreviousState().equals(State.JUMPING_FORWARD)
                    & !player1.getPreviousState().equals(State.JUMPING_BACK)
                    & !player1.getPreviousState().equals(State.JUMPING)) {
                player1.setCurrentState(State.MOVING_FORWARD);
                player1.setCurrentSpeed(player1.walkingForwardSpeed);
            }

            //jumping
            if (direction == 2.0 & (stickPreviousDirection != 2.0 & stickPreviousDirection != 3.0 & stickPreviousDirection != 1)
                    & (player1.getCurrentState().equals(State.STANDING)
                    || (player1.getPreviousState().equals(State.CROUCHING3) & player1.isAnimationFinished(player1.crouchingAnimation3)))) {
                player1.setCurrentState(State.JUMPING);
            }

            //jumping left and right
            if(stickPreviousDirection == 2 & direction == 3
                    & (player1.getPreviousState().equals(State.STANDING)
                    || player1.getCurrentState().equals(State.MOVING_FORWARD)
                    || player1.getCurrentState().equals(State.MOVING_BACK))){
                player1.setCurrentState(State.JUMPING_BACK);
                player1.setCurrentSpeed(player1.jumpingBackSpeed);
            }
            if(stickPreviousDirection == 2 & direction == 1
                    & (player1.getPreviousState().equals(State.STANDING)
                    || player1.getCurrentState().equals(State.MOVING_FORWARD)
                    || player1.getCurrentState().equals(State.MOVING_BACK))){
                player1.setCurrentState(State.JUMPING_FORWARD);
                player1.setCurrentSpeed(player1.jumpingForwardSpeed);
            }


            if(player1.getPreviousState().equals(State.JUMPING) & player1.isAnimationFinished(player1.jumpingAnimation)
                    || player1.getPreviousState().equals(State.JUMPING_BACK) & player1.isAnimationFinished(player1.jumpingLeftAnimation)
                    || player1.getPreviousState().equals(State.JUMPING_FORWARD) & player1.isAnimationFinished(player1.jumpingRightAnimation)) {
                player1.setCurrentState(State.CROUCHING3);
            }

            if (!stick.isTouched()
                    & !player1.getCurrentState().equals(State.JUMPING)
                    & !player1.getCurrentState().equals(State.JUMPING_BACK)
                    & !player1.getCurrentState().equals(State.JUMPING_FORWARD)
                    & !player1.getCurrentState().equals(State.CROUCHING1)
                    & !player1.getCurrentState().equals(State.CROUCHING3)){
                player1.setCurrentState(State.STANDING);
                player1.setCurrentSpeed(0);
            }
            if(stick.isTouched()
                    & stick.getKnobPercentX() == 0
                    & !player1.getCurrentState().equals(State.JUMPING)
                    & !player1.getCurrentState().equals(State.JUMPING_BACK)
                    & !player1.getCurrentState().equals(State.JUMPING_FORWARD)
                    & !player1.getCurrentState().equals(State.CROUCHING1)
                    & !player1.getCurrentState().equals(State.CROUCHING3)){
                player1.setCurrentState(State.STANDING);
                player1.setCurrentSpeed(0);
            }

            if(player1.getPreviousState().equals(State.CROUCHING3) & player1.isAnimationFinished(player1.crouchingAnimation3)) {
                player1.setCurrentState(State.STANDING);
                player1.setCurrentSpeed(0);
            }

            //moving while jumping
//            if((direction == 3.0 || direction == -4 || direction == 4) & player1.jumping == true & player1.crouching == false) {
//                player1.player_body.setLinearVelocity(-8f, player1.player_body.getLinearVelocity().y);
//            }
//            if((direction == 1 || direction == 0) & player1.jumping == true & player1.crouching == false)
//                player1.player_body.setLinearVelocity(8f, player1.player_body.getLinearVelocity().y);

            stickPreviousDirection = stickCurrentDirection;

            setPlayerBodySpeedByState(player1);

// 4 inputs
// down = -1, left = -2 and 2, up = 1, right = 0;

//            float dx = stick.getKnobPercentX();
//            float dy = stick.getKnobPercentY();

//            double direction = Math.floor((Math.atan2(dy, dx) + Math.PI / 4) / (2 * Math.PI / 4));

//            if (direction >= 4) direction = 0;
//            double angle = direction * (Math.PI / 4);

//            if (direction == -1.0) {
//                player1.crouching = true;
//            }
//            if ((direction == -2.0 || direction == 2.0) && player1.crouching == false) {
//                player1.player_body.setLinearVelocity(-5f, 0);
//            }
//            if (direction == 1.0) {
//                player1.jumping = true;
//            }
//            if ((stick.isTouched() & direction == 0.0 & stick.getKnobPercentX() != 0) && player1.crouching == false) {
//                player1.player_body.setLinearVelocity(6f, 0);
//            }
//            if (!stick.isTouched()) {
//                player1.crouching = false;
//                player1.player_body.setLinearVelocity(0, player1.player_body.getLinearVelocity().y);
//            }

        }


    }

    private void handlePlayer2Inputs(float dt){
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player2.crouching == false) {
           player2.setCurrentState(Player.State.JUMPING);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player2.crouching == false) {
            player2.setCurrentState(Player.State.MOVING_FORWARD);
            player2.setCurrentSpeed(player2.walkingForwardSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player2.crouching == false) {
            player2.setCurrentState(Player.State.MOVING_BACK);
            player2.setCurrentSpeed(player2.walkingBackSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
             player2.crouching = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            player2.setCurrentState(Player.State.JUMPING_BACK);
            player2.setCurrentSpeed(player2.jumpingBackSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            player2.setCurrentState(Player.State.JUMPING_FORWARD);
            player2.setCurrentSpeed(player2.jumpingForwardSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            player2.setCurrentState(Player.State.STANDING);
            player2.setCurrentSpeed(0);
    } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
       player2.onFrame(player2.jumpingAnimation, 10);
    }
        else {
            player2.crouching = false;
            player2.getPlayer_body().setLinearVelocity(0, player2.getPlayer_body().getLinearVelocity().y);
            player2.setCurrentState(Player.State.STANDING);
           // player2.setCurrentSpeed(0);
        }

        setPlayerBodySpeedByState(player2);
    }

    public void setPlayerBodySpeedByState(Player player) {
        if (player.getCurrentState().equals(State.MOVING_FORWARD)) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), 0);
        } else if (player.getCurrentState().equals(State.MOVING_BACK)) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), 0);
        } else if (player.getCurrentState().equals(State.STANDING)) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), player.getPlayer_body().getLinearVelocity().y);
        } else if (player.getCurrentState().equals(State.JUMPING) & player.getPlayer_body().getPosition().y <= player.maxJumpHeight & player.getPlayer_body().getLinearVelocity().y >= 0) {
            player.getPlayer_body().setLinearVelocity(0, 15);
        } else if (player.getCurrentState().equals(State.JUMPING) & player.getPlayer_body().getPosition().y >= player.maxJumpHeight) {
            player.getPlayer_body().setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(State.JUMPING_BACK) & player.getPlayer_body().getPosition().y <= player.maxJumpHeight & player.getPlayer_body().getLinearVelocity().y >= 0) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), 11);
        } else if (player.getCurrentState().equals(State.JUMPING_BACK) & player.getPlayer_body().getPosition().y >= player.maxJumpHeight) {
            player.getPlayer_body().setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(State.JUMPING_BACK) & player.getPlayer_body().getLinearVelocity().y < 0) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), -20);
        }else if (player.getCurrentState().equals(State.JUMPING_FORWARD) & player.getPlayer_body().getPosition().y <= player.maxJumpHeight & player.getPlayer_body().getLinearVelocity().y >= 0) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), 11);
        } else if (player.getCurrentState().equals(State.JUMPING_FORWARD) & player.getPlayer_body().getPosition().y >= player.maxJumpHeight) {
            player.getPlayer_body().setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(State.JUMPING_FORWARD) & player.getPlayer_body().getLinearVelocity().y < 0) {
            player.getPlayer_body().setLinearVelocity(player.getCurrentSpeed(), -20);
        }

        if(player.getCurrentState() != State.JUMPING & player.getCurrentState() != State.JUMPING_BACK & player.getCurrentState() != State.JUMPING_FORWARD & player.getPlayer_body().getPosition().y > 2.21){
            player.getPlayer_body().setLinearVelocity(0, -11);
        }
    }

    public void handleStaff() {

        //check if players overlap each other on X-Axis
        if(player1.getPlayer_body().getPosition().x > player2.getPlayer_body().getPosition().x - (player2.getPlayerWidth()/2 + player1.getPlayerWidth()/2)/StreetFighter.PPM
                & player1.getPlayer_body().getPosition().x < player2.getPlayer_body().getPosition().x + (player2.getPlayerWidth()/2 + player1.getPlayerWidth()/2)/StreetFighter.PPM ) {

            float distance = player1.getPlayer_body().getPosition().x > player2.getPlayer_body().getPosition().x
                    ? player1.getPlayer_body().getPosition().x - player2.getPlayer_body().getPosition().x
                    : player2.getPlayer_body().getPosition().x - player1.getPlayer_body().getPosition().x;


            if((player1.getCurrentState() == Player.State.JUMPING_FORWARD || player1.getCurrentState() == Player.State.JUMPING_BACK)
                    || (player2.getCurrentState() == Player.State.JUMPING_FORWARD || player2.getCurrentState() == Player.State.JUMPING_BACK)) {

                overlaped = true;

                float speed = 18f;

                if(player1.getPlayer_body().getLinearVelocity().y < 0) {
                    if (player1.isPlayer1Side)
                        player2.setCurrentSpeed(speed - (distance * 7));

                    if (!player1.isPlayer1Side)
                        player2.setCurrentSpeed(-speed + (distance * 7));
                }

//                if(player2.getPlayer_body().getLinearVelocity().y < 0) {
//                    if (player2.isPlayer1Side)
//                        player1.setCurrentSpeed(speed - (distance * 7));
//
//                    if (!player2.isPlayer1Side)
//                        player1.setCurrentSpeed(-speed + (distance * 7));
//                }
            }

        }


        if(player1.getPlayer_body().getPosition().x <= player2.getPlayer_body().getPosition().x - (player2.getPlayerWidth()/2 + player1.getPlayerWidth()/2)/StreetFighter.PPM
                || player1.getPlayer_body().getPosition().x >= player2.getPlayer_body().getPosition().x + (player2.getPlayerWidth()/2 + player1.getPlayerWidth()/2)/StreetFighter.PPM) {

            if(overlaped == true) {
                overlaped = false;

                if(player1.getPlayer_body().getLinearVelocity().y < 0)
                    if (player2.getCurrentSpeed() != 0) player2.setCurrentSpeed(0);

                if(player2.getPlayer_body().getLinearVelocity().y < 0)
                    if (player1.getCurrentSpeed() != 0) player1.setCurrentSpeed(0);

            }
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
        Gdx.app.log("Resolution: ", width + " " + height);
    }

    public void update(float dt){
     handleInput(dt);

     handleStaff();

     world.step(1/60f,6,2);

     player1.update(dt);
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
            player1.draw(game.batch);
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
          player1.dispose();
          player2.dispose();
          manager.dispose();
    }

}

