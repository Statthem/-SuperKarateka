package com.mygdx.game.creators;

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

public class WorldCreator {
    //Map related variables
    protected static TmxMapLoader mapLoader;
    public static TiledMap map;
    public static OrthogonalTiledMapRenderer mapRenderer;

    //Box2D variables
    public static World world;
    public static Box2DDebugRenderer b2dr;

    private static final int worldGravity = -10; // -10 - default

    public WorldCreator(){
        world = new World(new Vector2(0, worldGravity), true);
    }

    public static void createWorld(){
        b2dr = new Box2DDebugRenderer();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("StreetFighter3_Resources/96-54(Orthogonal).tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/StreetFighter.PPM);

        //Create ground
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




}
