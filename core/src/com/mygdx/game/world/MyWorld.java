package com.mygdx.game.world;

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
import com.mygdx.game.StreetFighter;

public class MyWorld {
    //Map related variables
    protected static TmxMapLoader mapLoader;
    public static TiledMap map;
    public static OrthogonalTiledMapRenderer mapRenderer;

    //Box2D variables
    public static World world;
    public static Box2DDebugRenderer b2dr;

    private Body leftBorder;
    private Body rightBorder;
    private Body ground;

    private Rectangle groundRectangle;

    private static final int worldGravity = 0; // -10 - default

    public MyWorld(){
        world = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, worldGravity), true);
        b2dr = new Box2DDebugRenderer();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("StreetFighter3_Resources/96-54(Orthogonal).tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/StreetFighter.PPM);

        createGround();
        createBorders();
    }

    private void createGround(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

       // for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            groundRectangle = ((RectangleMapObject) map.getLayers().get(1).getObjects().get(0)).getRectangle();//(RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((groundRectangle.getX() + groundRectangle.getWidth()/2)/StreetFighter.PPM, (groundRectangle.getY() + groundRectangle.getHeight()/2)/StreetFighter.PPM);

            ground = world.createBody(bdef);

            shape.setAsBox((groundRectangle.getWidth()/2)/StreetFighter.PPM, (groundRectangle.getHeight()/2)/StreetFighter.PPM);
            fdef.shape = shape;
            ground.createFixture(fdef);
        }




    private void createBorders(){
        BodyDef leftBorderBody = new BodyDef();
        leftBorderBody.position.set(0f, 5.9f); // x, y of body center
        leftBorderBody.type = BodyDef.BodyType.StaticBody;
        leftBorder = world.createBody(leftBorderBody);

        BodyDef rightBorderBody = new BodyDef();
        rightBorderBody.position.set(19.18f, 5.88f); // x, y of body center
        rightBorderBody.type = BodyDef.BodyType.StaticBody;
        rightBorder = world.createBody(rightBorderBody);

        PolygonShape border_shape = new PolygonShape();
        border_shape.setAsBox(1/StreetFighter.PPM, 1080f/StreetFighter.PPM);

        FixtureDef leftBorderFixtureDef = new FixtureDef();
        leftBorderFixtureDef.shape = border_shape;
        leftBorder.createFixture(leftBorderFixtureDef);

        FixtureDef rightBorderFixtureDef = new FixtureDef();
        rightBorderFixtureDef.shape = border_shape;
        rightBorder.createFixture(rightBorderFixtureDef);
    }

    public float getGroundUpperSideY(){
        return((groundRectangle.getY() + groundRectangle.getHeight())/StreetFighter.PPM);
    }




}
