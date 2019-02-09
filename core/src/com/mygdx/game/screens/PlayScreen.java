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

    public void handleInput(float dt) {
             handlePlayer1Inputs(dt);
             handlePlayer2Inputs(dt);
    }

    private void handlePlayer1Inputs(float dt){
        if(enableAndroidControls) {

            //  player1.crouching = false;
            player1.player_body.setLinearVelocity(0, player1.player_body.getLinearVelocity().y);

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
                if ((player1.getPreviousState() == Player.State.STANDING
                        || player1.getPreviousState() == Player.State.MOVING_RIGHT
                        || player1.getPreviousState() == Player.State.MOVING_LEFT
                        || player1.getPreviousState() == Player.State.CROUCHING1)) {
                    player1.setCurrentState(Player.State.CROUCHING1);
                }
                if ((player1.getPreviousState() == Player.State.CROUCHING1
                        & player1.getCurrentState() == Player.State.CROUCHING1
                        & player1.isAnimationFinished(player1.crouchingAnimation1))
                        || player1.getPreviousState() == Player.State.CROUCHING2){
                    player1.setCurrentState(Player.State.CROUCHING2);
                }
            }
            if(player1.crouching == false ){
                if(player1.getPreviousState() == Player.State.CROUCHING1
                        & !player1.isAnimationFinished(player1.crouchingAnimation1)) {
                    player1.setCurrentState(Player.State.CROUCHING1);
                }
                if((player1.getPreviousState() == Player.State.CROUCHING1
                        & player1.isAnimationFinished(player1.crouchingAnimation1))
                        ||  player1.getPreviousState() == Player.State.CROUCHING2
                        || player1.getPreviousState() == Player.State.CROUCHING3) {
                    player1.setCurrentState(Player.State.CROUCHING3);
                }
            }

            if ((direction == -3.0 || direction == 3.0 || direction == -4.0 || direction == 4.0)
                    & !player1.getPreviousState().equals(Player.State.CROUCHING3)
                    & !player1.getPreviousState().equals(Player.State.CROUCHING2)
                    & !player1.getPreviousState().equals(Player.State.CROUCHING1)
                    & !player1.getPreviousState().equals(Player.State.JUMPINGRIGHT)
                    & !player1.getPreviousState().equals(Player.State.JUMPINGLEFT)
                    & !player1.getPreviousState().equals(Player.State.JUMPING)) {
                player1.setCurrentState(Player.State.MOVING_LEFT);
            }
            if ((stick.isTouched() & (direction == 0.0 || direction == 1 || direction == -1) & stick.getKnobPercentX() != 0)  & !player1.getPreviousState().equals(Player.State.CROUCHING3)
                    & !player1.getPreviousState().equals(Player.State.CROUCHING2)
                    & !player1.getPreviousState().equals(Player.State.CROUCHING1)
                    & !player1.getPreviousState().equals(Player.State.JUMPINGRIGHT)
                    & !player1.getPreviousState().equals(Player.State.JUMPINGLEFT)
                    & !player1.getPreviousState().equals(Player.State.JUMPING)) {
                player1.setCurrentState(Player.State.MOVING_RIGHT);
            }

            //jumping
            if (direction == 2.0 & (stickPreviousDirection != 2.0 & stickPreviousDirection != 3.0 & stickPreviousDirection != 1)
                    & (player1.getCurrentState().equals(Player.State.STANDING)
                    || (player1.getPreviousState().equals(Player.State.CROUCHING3) & player1.isAnimationFinished(player1.crouchingAnimation3)))) {
                player1.setCurrentState(Player.State.JUMPING);
            }

            //jumping left and right
            if(stickPreviousDirection == 2 & direction == 3
                    & (player1.getPreviousState().equals(Player.State.STANDING)
                    || player1.getCurrentState().equals(Player.State.MOVING_RIGHT)
                    || player1.getCurrentState().equals(Player.State.MOVING_LEFT))){
                player1.setCurrentState(Player.State.JUMPINGLEFT);
            }
            if(stickPreviousDirection == 2 & direction == 1
                    & (player1.getPreviousState().equals(Player.State.STANDING)
                    || player1.getCurrentState().equals(Player.State.MOVING_RIGHT)
                    || player1.getCurrentState().equals(Player.State.MOVING_LEFT))){
                player1.setCurrentState(Player.State.JUMPINGRIGHT);
                player1.currentSpeed = 7;
            }


            if(player1.getPreviousState().equals(Player.State.JUMPING) & player1.isAnimationFinished(player1.jumpingAnimation)
                    || player1.getPreviousState().equals(Player.State.JUMPINGLEFT) & player1.isAnimationFinished(player1.jumpingLeftAnimation)
                    || player1.getPreviousState().equals(Player.State.JUMPINGRIGHT) & player1.isAnimationFinished(player1.jumpingRightAnimation)) {
                player1.setCurrentState(Player.State.CROUCHING3);
            }

            if (!stick.isTouched()
                    & !player1.getCurrentState().equals(Player.State.JUMPING)
                    & !player1.getCurrentState().equals(Player.State.JUMPINGLEFT)
                    & !player1.getCurrentState().equals(Player.State.JUMPINGRIGHT)
                    & !player1.getCurrentState().equals(Player.State.CROUCHING1)
                    & !player1.getCurrentState().equals(Player.State.CROUCHING3)){
                player1.setCurrentState(Player.State.STANDING);
            }
            if(stick.isTouched()
                    & stick.getKnobPercentX() == 0
                    & !player1.getCurrentState().equals(Player.State.JUMPING)
                    & !player1.getCurrentState().equals(Player.State.JUMPINGLEFT)
                    & !player1.getCurrentState().equals(Player.State.JUMPINGRIGHT)
                    & !player1.getCurrentState().equals(Player.State.CROUCHING1)
                    & !player1.getCurrentState().equals(Player.State.CROUCHING3)){
                player1.setCurrentState(Player.State.STANDING);
            }

            if(player1.getPreviousState().equals(Player.State.CROUCHING3) & player1.isAnimationFinished(player1.crouchingAnimation3)) {
                player1.setCurrentState(Player.State.STANDING);
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
            player2.setCurrentState(Player.State.MOVING_RIGHT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player2.crouching == false) {
            player2.setCurrentState(Player.State.MOVING_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
             player2.crouching = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            player2.setCurrentState(Player.State.JUMPINGLEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            player2.setCurrentState(Player.State.JUMPINGRIGHT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            System.out.println("mouse position.x " + (Gdx.input.getX()));
            System.out.println("mouse position.y " + (1080 - Gdx.input.getY()));
            player2.setCurrentState(Player.State.STANDING);
        }
        else {
            player2.crouching = false;
            player2.player_body.setLinearVelocity(0, player2.player_body.getLinearVelocity().y);
        }

        setPlayerBodySpeedByState(player2);
    }

    public void setPlayerBodySpeedByState(Player player) {
        if (player.getCurrentState().equals(Player.State.MOVING_RIGHT)) {
            player.player_body.setLinearVelocity(player.walkingForwardSpeed, 0);
        } else if (player.getCurrentState().equals(Player.State.MOVING_LEFT)) {
            player.player_body.setLinearVelocity(player.walkingBackSpeed, 0);
        } else if (player.getCurrentState().equals(Player.State.STANDING)) {
            player.player_body.setLinearVelocity(0, player.player_body.getLinearVelocity().y);
        } else if (player.getCurrentState().equals(Player.State.JUMPING) & player.player_body.getPosition().y <= player.maxJumpHeight & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(0, 15);
        } else if (player.getCurrentState().equals(Player.State.JUMPING) & player.player_body.getPosition().y >= player.maxJumpHeight) {
            player.player_body.setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getPosition().y <= player.maxJumpHeight & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(-11, 11);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getPosition().y >= player.maxJumpHeight) {
            player.player_body.setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGLEFT) & player.player_body.getLinearVelocity().y < 0) {
            player.player_body.setLinearVelocity(-8, -20);
        }else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getPosition().y <= player.maxJumpHeight & player.player_body.getLinearVelocity().y >= 0) {
            player.player_body.setLinearVelocity(player.currentSpeed, 11);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getPosition().y >= player.maxJumpHeight) {
            player.player_body.setLinearVelocity(0, -14);
        } else if (player.getCurrentState().equals(Player.State.JUMPINGRIGHT) & player.player_body.getLinearVelocity().y < 0) {
            player.player_body.setLinearVelocity(player.currentSpeed, -20);
        }else if (player.getCurrentState().equals(Player.State.COLIDING) & player.player_body.getLinearVelocity().y < 0) {
           System.out.println("coliding");
        }

        if(player.getCurrentState() != Player.State.JUMPING & player.getCurrentState() != Player.State.JUMPINGLEFT & player.getCurrentState() != Player.State.JUMPINGRIGHT & player.player_body.getPosition().y > 2.21){
            player.player_body.setLinearVelocity(0, -11);
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

