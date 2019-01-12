package com.mygdx.game.sprites;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.screens.PlayScreen;

import java.awt.Rectangle;

public class Player {
    public World world;
    public Body b2body;

    public Player(World world){
        this.world = world;
        definePlayer();
    }

    public void definePlayer(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(420/StreetFighter.PPM, 470/StreetFighter.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(130/StreetFighter.PPM,300/StreetFighter.PPM);


//        CircleShape shape = new CircleShape();
//        shape.setRadius(100);

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef);

    }
}
