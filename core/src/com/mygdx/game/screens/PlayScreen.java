package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.scenes.HUD;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.creators.WorldCreator;

import static com.mygdx.game.creators.WorldCreator.mapRenderer;
import static com.mygdx.game.creators.WorldCreator.b2dr;
import static com.mygdx.game.creators.WorldCreator.world;
import static com.mygdx.game.creators.WorldCreator.map;

public class PlayScreen implements Screen {

    private StreetFighter game;
    //Texture texture;
    private OrthographicCamera camera;

    //Viewport variables
    private Viewport viewport;
    private Viewport fitViewport;
    private Viewport fillViewport;
    private Viewport screenViewport;
    private Viewport extendViewport;

    Player player;

    private HUD hud;

    private TextureAtlas atlas;

    public PlayScreen(StreetFighter game){
        atlas = new TextureAtlas("StreetFighter3_Resources/Sprites/Ryu_Stance/ryu_stance.pack");

        this.game = game;

        //stf3 Ryu texture
        //texture = new Texture("frame_00_delay-0.06s.gif");

        //create camera
        camera = new OrthographicCamera();

        //camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        camera.position.set((StreetFighter.resolution_width/2)/StreetFighter.PPM, (StreetFighter.resolution_height/2)/StreetFighter.PPM, 0);

        //Viewport for maintaining aspect ratio
        viewport = new FillViewport(StreetFighter.resolution_width/StreetFighter.PPM, StreetFighter.resolution_height/StreetFighter.PPM, camera);
//        fitViewport = new FillViewport(800, 480, camera);
//        fillViewport = new FillViewport(800, 480, camera);
//        screenViewport = new ScreenViewport(camera);
//        extendViewport = new ExtendViewport(800, 480, camera);

       // extendViewport.apply();

        World world = null;
        WorldCreator streetFighterWorldCreator = new WorldCreator(world);
        streetFighterWorldCreator.createWorld();

        player = new Player(WorldCreator.world, this);

        hud = new HUD(game.batch);

    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            player.b2body.applyLinearImpulse(new Vector2(0,0.5f), player.b2body.getWorldCenter(), true);
            //camera.position.y += 20 + dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0), player.b2body.getWorldCenter(), true);
            //camera.position.y -= 20 + dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0), player.b2body.getWorldCenter(), true);
            //camera.position.y -= 20 + dt;
        }


        if(Gdx.input.isTouched() && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0), player.b2body.getWorldCenter(), true);
            //camera.position.y -= 20 + dt;
        }



//
//        if(Gdx.input.isKeyPressed(Input.Keys.PLUS)){
//            camera.zoom -= 0.01;
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.MINUS)){
//            camera.zoom += 0.01;
//        }
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

        //set camera to follow player
        //camera.position.x = player.b2body.getPosition().x;

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

       // printFrameRateDeviation(Gdx.graphics.getDeltaTime());
        update(delta);

        //clear the game screen with black
      Gdx.gl.glClearColor(0,0,0,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      //render(draw tiled map)
      mapRenderer.render();

      //render Box2dDebugLines
      b2dr.render(world, camera.combined);

      game.batch.setProjectionMatrix(camera.combined);


      /*
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

      */

      //draw our game Hud
        //hud.stage.draw();


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
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
}
