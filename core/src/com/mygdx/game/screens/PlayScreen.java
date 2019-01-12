package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.scenes.HUD;
import com.mygdx.game.sprites.Player;

public class PlayScreen implements Screen {

    private StreetFighter game;
    Texture texture;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport fitViewport;
    private Viewport fillViewport;
    private Viewport screenViewport;
    private Viewport extendViewport;
    private HUD hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2D variables
    private World world;
    private Box2DDebugRenderer b2dr;

    private Player player;


    public PlayScreen(StreetFighter game){
        this.game = game;
        texture = new Texture("frame_00_delay-0.06s.gif");

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

        hud = new HUD(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("StreetFighter3_Resources/192-108(Orthogonal).tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/StreetFighter.PPM);

        world = new World(new Vector2(0,-10), true);
        b2dr = new Box2DDebugRenderer();

        player = new Player(world);

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2)/StreetFighter.PPM, (rect.getY() + rect.getHeight()/2)/StreetFighter.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth()/2)/StreetFighter.PPM, (rect.getHeight()/2)/StreetFighter.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

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

    public void update(float dt){
     handleInput(dt);

     world.step(1/60f,6,2);

    // System.out.println(Gdx.graphics.getDeltaTime());

        //set camera to follow player
        //camera.position.x = player.b2body.getPosition().x;

     //update our camera with correct coordinates after changes
     camera.update();

     //tell our renderer to draw only what our camera can see
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

      //render(draw tiled map)
      renderer.render();

      //render Box2dDebugLines
      b2dr.render(world, camera.combined);

      game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

      //draw our game Hud
        //hud.stage.draw();


        game.batch.begin();
        game.batch.draw(texture, 20,80);
        game.batch.end();
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
        System.out.println(width + " " + height);
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
