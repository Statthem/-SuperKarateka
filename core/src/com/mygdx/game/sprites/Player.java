package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.screens.PlayScreen;

import java.awt.Rectangle;

public class Player extends Sprite {

    private static int ryuSprite_width = 80;
    private static int ryuSprite_height = 113;


    private static int player_lowBox_hx = 145;
    private static int player_lowBox_hy = 90;

    private static int player_midBox_hx = 95;
    private static int player_midBox_hy = 100;

    private static int player_highBox_hx = 125;
    private static int player_highBox_hy = 55;

    private static int player_headBox_hx = 25;
    private static int player_headBox_hy = 20;

    //private static float ryuSprite_scaleFactor = 1.5f;

    public World world;
    public Body b2body;
    private TextureRegion playerStand;

    public Player(World world, PlayScreen screen){
        //get region from our texture.pack file(ryu_stance.pack)
        super(screen.getAtlas().findRegion("sprite_sheet"));
        this.world = world;
        definePlayer();

        playerStand = new TextureRegion(getTexture(), 0,0,ryuSprite_width,ryuSprite_height);
        //set position and size for player sprite
        setBounds(360/StreetFighter.PPM, 370/StreetFighter.PPM,350/StreetFighter.PPM, 620/StreetFighter.PPM);
        setRegion(playerStand);
    }

    public void definePlayer(){
        BodyDef low_bodyDef = new BodyDef();
        low_bodyDef.position.set(4.2f, 4.7f); // x, y of body center
        low_bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(low_bodyDef);

        //lower body fixture
        FixtureDef low_fixtureDef = new FixtureDef();
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(player_lowBox_hx/StreetFighter.PPM,player_lowBox_hy/StreetFighter.PPM);
        low_fixtureDef.shape = low_shape;
        b2body.createFixture(low_fixtureDef);

        //mid body fixture
        FixtureDef mid_fixtureDef = new FixtureDef();
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(player_midBox_hx/StreetFighter.PPM,player_midBox_hy/StreetFighter.PPM,
                new Vector2(0, player_midBox_hx/StreetFighter.PPM * 2),
                0);
        mid_fixtureDef.shape = mid_shape;
        b2body.createFixture(mid_fixtureDef);

        //high body fixture
        FixtureDef high_fixtureDef = new FixtureDef();
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(player_highBox_hx/StreetFighter.PPM,player_highBox_hy/StreetFighter.PPM,
                new Vector2(0, (player_midBox_hx/StreetFighter.PPM * 2) + 1.6f),
                0);
        high_fixtureDef.shape = high_shape;
        b2body.createFixture(high_fixtureDef);

        //head body fixture
        FixtureDef head_fixtureDef = new FixtureDef();
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(player_headBox_hx/StreetFighter.PPM,player_headBox_hy/StreetFighter.PPM,
                new Vector2(0, (player_midBox_hx/StreetFighter.PPM * 2) + 1.6f + 1.25f),
                0);
        head_fixtureDef.shape = high_shape;
        b2body.createFixture(head_fixtureDef);

    }




    public void update(float delta){
        //set sprite position on box
        setPosition(b2body.getPosition().x - getWidth() / 2, (b2body.getPosition().y - getHeight()/2) + 2.1f);

    }
}
