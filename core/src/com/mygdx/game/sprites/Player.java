package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.creators.WorldCreator;
import com.mygdx.game.screens.PlayScreen;

import java.awt.Rectangle;

public class Player extends Sprite {
    public enum State {STANDING, WALKING, JUMPING, FALLING, FIGHTING, HITSTUN}
    public State currentState;
    public State previousState;

    private Animation standAnim;
    private Animation jumpAnim;
    private Animation wolkAnim;
    private float stateTimer;

    private boolean leftToRight;

    private static final int ryuSprite_width = 78;
    private static final int ryuSprite_height = 111;

    //134
    //186


    private static int player_lowBox_hx = 145;
    private static int player_lowBox_hy = 90;

    private static int player_midBox_hx = 95;
    private static int player_midBox_hy = 100;

    private static int player_highBox_hx = 125;
    private static int player_highBox_hy = 55;

    private static int player_headBox_hx = 38;
    private static int player_headBox_hy = 35;

    //private static float ryuSprite_scaleFactor = 1.5f;

    private World world;
    public Body player1_body;
    public Body player2_body;
    private TextureRegion playerStand;



    public Player(PlayScreen screen){
        super(screen.getAtlas().findRegion("sprite_sheet"));
        this.world = WorldCreator.world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        leftToRight = true;

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 2,2,ryuSprite_width,ryuSprite_height));
        for(int i = 1; i < 10; i++){
            frames.add(new TextureRegion(getTexture(), i * ryuSprite_width + 2, 2, ryuSprite_width,ryuSprite_height));
        }
        standAnim = new Animation(0.065f, frames); //0.1 default (less = faster)
        frames.clear();

        createPlayer1();
        createPlayer2();

        //hz
        playerStand = new TextureRegion(getTexture(), 0,0,ryuSprite_width,ryuSprite_height);

        //set position and size for player sprite
        setBounds(0, 0,350/StreetFighter.PPM, 620/StreetFighter.PPM);
        setRegion(playerStand);
    }

    public void update(float delta){
        //set sprite position on box
        setPosition(player1_body.getPosition().x - getWidth() / 2, (player1_body.getPosition().y - getHeight()/2) + 2.1f);
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta){
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case STANDING:
                region = (TextureRegion) standAnim.getKeyFrame(stateTimer, true);
                break;
//            case WALKING:
//                region = (TextureRegion) wolkAnim.getKeyFrame(stateTimer, true);
//                break;
                default :
                    region = (TextureRegion) standAnim.getKeyFrame(stateTimer, true);
                    break;
        }
         // rotate sprite on x
        //region.flip(true,false);

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(player1_body.getLinearVelocity().y > 0)
            return State.JUMPING;
       else if(player1_body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(player1_body.getLinearVelocity().x != 0)
            return State.WALKING;
        else
            return State.STANDING;

    }

    public void createPlayer1(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(4.2f, 4.7f); // x, y of body center
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player1_body = world.createBody(bodyDef);

        //lower body fixture
        FixtureDef low_fixtureDef = new FixtureDef();
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(player_lowBox_hx/StreetFighter.PPM,player_lowBox_hy/StreetFighter.PPM);
        low_fixtureDef.shape = low_shape;
        player1_body.createFixture(low_fixtureDef);

        //mid body fixture
        FixtureDef mid_fixtureDef = new FixtureDef();
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(player_midBox_hx/StreetFighter.PPM,player_midBox_hy/StreetFighter.PPM,
                new Vector2(0,  player_lowBox_hy/StreetFighter.PPM + player_midBox_hy/StreetFighter.PPM),
                0);
        mid_fixtureDef.shape = mid_shape;
        player1_body.createFixture(mid_fixtureDef);

        //high body fixture
        FixtureDef high_fixtureDef = new FixtureDef();
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(player_highBox_hx/StreetFighter.PPM,player_highBox_hy/StreetFighter.PPM,
                new Vector2(0, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + player_highBox_hy/StreetFighter.PPM)),
                0);
        high_fixtureDef.shape = high_shape;
        player1_body.createFixture(high_fixtureDef);

        //head body fixture
        FixtureDef head_fixtureDef = new FixtureDef();
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(player_headBox_hx/StreetFighter.PPM,player_headBox_hy/StreetFighter.PPM,
                new Vector2(0.25f, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + (player_highBox_hy/StreetFighter.PPM * 2) + player_headBox_hy/StreetFighter.PPM)),
                0);
        head_fixtureDef.shape = head_shape;
        player1_body.createFixture(head_fixtureDef);

    }

    public void createPlayer2(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(15.2f, 4.7f); // x, y of body center
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player2_body= world.createBody(bodyDef);

        //lower body fixture
        FixtureDef low_fixtureDef = new FixtureDef();
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(player_lowBox_hx/StreetFighter.PPM,player_lowBox_hy/StreetFighter.PPM);
        low_fixtureDef.shape = low_shape;
        player2_body.createFixture(low_fixtureDef);

        //mid body fixture
        FixtureDef mid_fixtureDef = new FixtureDef();
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(player_midBox_hx/StreetFighter.PPM,player_midBox_hy/StreetFighter.PPM,
                new Vector2(0,  player_lowBox_hy/StreetFighter.PPM + player_midBox_hy/StreetFighter.PPM),
                0);
        mid_fixtureDef.shape = mid_shape;
        player2_body.createFixture(mid_fixtureDef);

        //high body fixture
        FixtureDef high_fixtureDef = new FixtureDef();
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(player_highBox_hx/StreetFighter.PPM,player_highBox_hy/StreetFighter.PPM,
                new Vector2(0, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + player_highBox_hy/StreetFighter.PPM)),
                0);
        high_fixtureDef.shape = high_shape;
        player2_body.createFixture(high_fixtureDef);

        //head body fixture
        FixtureDef head_fixtureDef = new FixtureDef();
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(player_headBox_hx/StreetFighter.PPM,player_headBox_hy/StreetFighter.PPM,
                new Vector2(-0.25f, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + (player_highBox_hy/StreetFighter.PPM * 2) + player_headBox_hy/StreetFighter.PPM)),
                0);
        head_fixtureDef.shape = head_shape;
        player2_body.createFixture(head_fixtureDef);

    }



}
