package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.world.MyWorld;
import com.mygdx.game.extended.MyTextureRegion;
import com.mygdx.game.screens.PlayScreen;

import java.util.HashMap;
import java.util.Map;


public abstract class Player extends Sprite implements Disposable{
    public enum State {STANDING, CROUCHING1, CROUCHING2, CROUCHING3, MOVING_FORWARD, MOVING_BACK, JUMPING, JUMPING_FORWARD, JUMPING_BACK, FALLING, FIGHTING, HITSTUN, COLIDING}
    private State currentState;
    private State previousState;

    protected PlayScreen screen;
    protected MyWorld myWorld;

    protected int sprite_width;
    protected int sprite_height;

    protected int standing_lowBox_hx;
    protected int standing_lowBox_hy;
    protected int standing_midBox_hx;
    protected int standing_midBox_hy;
    protected int standing_highBox_hx;
    protected int standing_highBox_hy;
    protected int standing_headBox_hx;
    protected int standing_headBox_hy;

    protected int crouching_lowBox_hx;
    protected int crouching_lowBox_hy;
    protected int crouching_midBox_hx;
    protected int crouching_midBox_hy;
    protected int crouching_highBox_hx;
    protected int crouching_highBox_hy;
    protected int crouching_headBox_hx;
    protected int crouching_headBox_hy;

    protected float headTurn;

    protected float widthScalingFactor = 3.58f; //343(4.4)  //280(3.58)
    protected float heightScalingFactor = 4.5f; //610.5(5.5)  //500(4.5) // 610:343 = 1.778 //4.4 // 3.52

    protected float scaledWidht;

    protected Array<Animation> playerAnimations;
    protected Map<String, MyTextureRegion> textureRegionMap;

    public TextureAtlas basicAtlas;
    public TextureAtlas jumpingAtlas;
    public TextureAtlas jumpingLeftAtlas;
    public TextureAtlas jumpingRightAtlas;

    public Animation standingAnimation;
    public Animation movingLeftAnimation;
    public Animation movingRightAnimation;
    public Animation crouchingAnimation1;
    public Animation crouchingAnimation2;
    public Animation crouchingAnimation3;
    public Animation jumpingAnimation;
    public Animation jumpingLeftAnimation;
    public Animation jumpingRightAnimation;

    private float stateTimer;

    public boolean crouching;
    public boolean jumping;

    public boolean isPlayer1Side;

    private com.badlogic.gdx.physics.box2d.World world;
    public Body player_body;

    private float lastPositionY;
    private float lastPositionX;

    public float maxJumpHeight = 7f;
    public float walkingBackSpeed = -5f;
    public float walkingForwardSpeed = 6f;
    public float jumpingForwardSpeed = 7f;
    public float jumpingBackSpeed = -10f;

    public float currentSpeed;


    public Player(PlayScreen screen, boolean isPlayer1, MyWorld myWorld){
        super();
        this.screen = screen;
        this.myWorld = myWorld;
        this.world = MyWorld.world;


        String simpleClassName = this.getClass().getSimpleName();

        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas", TextureAtlas.class);
        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_Jumping_pack.atlas", TextureAtlas.class);
        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_jumpingLeft.atlas", TextureAtlas.class);
        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_jumpingRight.atlas", TextureAtlas.class);
        screen.manager.finishLoading();

        basicAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas");
        jumpingAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_Jumping_pack.atlas");
        jumpingLeftAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_jumpingLeft.atlas");
        jumpingRightAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_jumpingRight.atlas");

        isPlayer1Side = isPlayer1;

        crouching = false;

        jumping = false;

        textureRegionMap = new HashMap<>();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
    }

    protected abstract void setFixtures();
    protected abstract void populateTextureRegionMap();

    protected void createPlayer() {
        BodyDef bodyDef = new BodyDef();

        float playerPositionX;
        float playerPositionY;

        String userData = "";

        if (!isPlayer1Side) {
            playerPositionX = 8.2f;
            playerPositionY = myWorld.getGroundUpperSideY() + standing_lowBox_hy / StreetFighter.PPM;
            userData = "player2";
        } else {
            playerPositionX = 4.2f;
            playerPositionY = myWorld.getGroundUpperSideY() + standing_lowBox_hy / StreetFighter.PPM;
            userData = "player1";
        }

        headTurn = 0.25f;

        int lowBox_hx;
        int lowBox_hy;
        int midBox_hx;
        int midBox_hy;
        int highBox_hx;
        int highBox_hy;
        int headBox_hx;
        int headBox_hy;

        if (crouching == false) {
            lowBox_hx = standing_lowBox_hx;
            lowBox_hy = standing_lowBox_hy;
            midBox_hx = standing_midBox_hx;
            midBox_hy = standing_midBox_hy;
            highBox_hx = standing_highBox_hx;
            highBox_hy = standing_highBox_hy;
            headBox_hx = standing_headBox_hx;
            headBox_hy = standing_headBox_hy;
        } else {
            lowBox_hx = crouching_lowBox_hx;
            lowBox_hy = crouching_lowBox_hy;
            midBox_hx = crouching_midBox_hx;
            midBox_hy = crouching_midBox_hy;
            highBox_hx = crouching_highBox_hx;
            highBox_hy = crouching_highBox_hy;
            headBox_hx = crouching_headBox_hx;
            headBox_hy = crouching_headBox_hy;
        }
        bodyDef.position.set(playerPositionX, playerPositionY); // x, y of body center
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player_body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        //fixtureDef.isSensor = true;
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(lowBox_hx / StreetFighter.PPM, lowBox_hy / StreetFighter.PPM, new Vector2(0, 0), 0);
        fixtureDef.shape = low_shape;
        player_body.createFixture(fixtureDef).setUserData("low");

        fixtureDef.isSensor = true;
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(midBox_hx / StreetFighter.PPM, midBox_hy / StreetFighter.PPM,
                new Vector2(0, lowBox_hy / StreetFighter.PPM + midBox_hy / StreetFighter.PPM),
                0);
        fixtureDef.shape = mid_shape;
        player_body.createFixture(fixtureDef).setUserData("mid");

//        EdgeShape mid_upper_edge = new EdgeShape();
//        mid_upper_edge.set(new Vector2(100/StreetFighter.PPM, lowBox_hy / StreetFighter.PPM + (midBox_hy * 2)/ StreetFighter.PPM), new Vector2(-100/StreetFighter.PPM,lowBox_hy / StreetFighter.PPM + (midBox_hy * 2)/ StreetFighter.PPM));
//        fixtureDef.shape = mid_upper_edge;
//        player_body.createFixture(fixtureDef).setUserData("mid_upper_edge");

        fixtureDef.isSensor = true;
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(highBox_hx / StreetFighter.PPM, highBox_hy / StreetFighter.PPM,
                new Vector2(0, (lowBox_hy / StreetFighter.PPM + (midBox_hy / StreetFighter.PPM * 2) + highBox_hy / StreetFighter.PPM)),
                0);
        fixtureDef.shape = high_shape;
        player_body.createFixture(fixtureDef).setUserData("high");

        fixtureDef.isSensor = true;
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(headBox_hx / StreetFighter.PPM, headBox_hy / StreetFighter.PPM,
                new Vector2(headTurn, (lowBox_hy / StreetFighter.PPM + (midBox_hy / StreetFighter.PPM * 2) + (highBox_hy / StreetFighter.PPM * 2) + headBox_hy / StreetFighter.PPM)),
                0);
        fixtureDef.shape = head_shape;
        player_body.createFixture(fixtureDef).setUserData("head");

        player_body.setUserData(userData);
    }

    public void changeFixturesShape(){
        Array<Fixture> fixtures = player_body.getFixtureList();
        if(fixtures.size != 0){

            int lowBox_hx; int lowBox_hy;
            int midBox_hx; int midBox_hy;
            int highBox_hx; int highBox_hy;
            int headBox_hx; int headBox_hy;

            if(crouching == false) {
                lowBox_hx = standing_lowBox_hx;
                lowBox_hy = standing_lowBox_hy;
                midBox_hx = standing_midBox_hx;
                midBox_hy = standing_midBox_hy;
                highBox_hx = standing_highBox_hx;
                highBox_hy = standing_highBox_hy;
                headBox_hx = standing_headBox_hx;
                headBox_hy = standing_headBox_hy;
            } else {
                lowBox_hx = crouching_lowBox_hx;
                lowBox_hy = crouching_lowBox_hy;
                midBox_hx = crouching_midBox_hx;
                midBox_hy = crouching_midBox_hy;
                highBox_hx = crouching_highBox_hx;
                highBox_hy = crouching_highBox_hy;
                headBox_hx = crouching_headBox_hx;
                headBox_hy = crouching_headBox_hy;
            }

            PolygonShape low_shape = (PolygonShape) fixtures.get(0).getShape();
            PolygonShape mid_shape = (PolygonShape) fixtures.get(1).getShape();
            PolygonShape high_shape = (PolygonShape) fixtures.get(2).getShape();
            PolygonShape head_shape = (PolygonShape) fixtures.get(3).getShape();


            low_shape.setAsBox(lowBox_hx/StreetFighter.PPM, lowBox_hy/StreetFighter.PPM,
                    new Vector2(0, 0),
                    0);
            mid_shape.setAsBox(midBox_hx/StreetFighter.PPM, midBox_hy/StreetFighter.PPM,
                    new Vector2(0,  lowBox_hy /StreetFighter.PPM + midBox_hy /StreetFighter.PPM),
                    0);
            high_shape.setAsBox(highBox_hx/StreetFighter.PPM, highBox_hy/StreetFighter.PPM,
                    new Vector2(0, (lowBox_hy /StreetFighter.PPM + (midBox_hy /StreetFighter.PPM * 2) + highBox_hy /StreetFighter.PPM)),
                    0);
            head_shape.setAsBox(headBox_hx /StreetFighter.PPM, headBox_hy /StreetFighter.PPM,
                    new Vector2(headTurn, (lowBox_hy /StreetFighter.PPM + (midBox_hy /StreetFighter.PPM * 2) + (highBox_hy /StreetFighter.PPM * 2) + headBox_hy /StreetFighter.PPM)),
                    0);
        }
    }

    public Animation getAnimation(String key, float frameDuration){
        TextureRegion textureRegion = textureRegionMap.get(key);
        super.setRegion(textureRegion);

        int x = getRegionX();
        int y = getRegionY();

        Array<TextureRegion> frames = new Array<>();
            //add first frame
            frames.add(new TextureRegion(getTexture(), x, y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
            for (int i = 1; i < ((MyTextureRegion) textureRegion).getFramesAmount(); i++) {
                frames.add(new TextureRegion(getTexture(), x + i * ((MyTextureRegion) textureRegion).getWidth(), y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
            }
            Animation animation = new Animation(frameDuration, frames); //0.1 default (less = faster)
            frames.clear();

            return animation;
    }

    public Animation getAnimation(String key, int from, int to){
        TextureRegion textureRegion = textureRegionMap.get(key);
        super.setRegion(textureRegion);

        int x = getRegionX();
        int y = getRegionY();

        Array<TextureRegion> frames = new Array<>();
        //add first frame
        for (int i = from; i <= to; i++) {
            if(i == 0)  frames.add(new TextureRegion(getTexture(), x + ((MyTextureRegion) textureRegion).getWidth(), y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
            else
            frames.add(new TextureRegion(getTexture(), x + i * ((MyTextureRegion) textureRegion).getWidth(), y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
        }
        Animation animation = new Animation(0.65f, frames); //0.1 default (less = faster)
        frames.clear();

        return animation;
    }

    public Animation getAnimation(String key, int from, int to, float frameDuration){
        TextureRegion textureRegion = textureRegionMap.get(key);
        super.setRegion(textureRegion);

        int x = getRegionX();
        int y = getRegionY();

        Array<TextureRegion> frames = new Array<>();
        //add first frame
        for (int i = from; i <= to; i++) {
            if(i == 0)  frames.add(new TextureRegion(getTexture(), x + ((MyTextureRegion) textureRegion).getWidth(), y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
            else
                frames.add(new TextureRegion(getTexture(), x + i * ((MyTextureRegion) textureRegion).getWidth(), y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
        }
        Animation animation = new Animation(frameDuration, frames); //0.1 default (less = faster)
        frames.clear();

        return animation;
    }

    public Animation getJumpingAnimation(String key,float frameDuration){
        TextureRegion textureRegion = textureRegionMap.get(key);
        super.setRegion(textureRegion);

        int x = getRegionX();
        int y = getRegionY();

        int regionHeight = getRegionHeight()/2;
        int regionWight = getRegionWidth()/2;

        setRegionHeight(regionHeight);
        setRegionWidth(regionWight);

        Array<TextureRegion> frames = new Array<>();
        //add first frame
        frames.add(new TextureRegion(getTexture(), x, y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
        for (int i = 1; i < ((MyTextureRegion) textureRegion).getFramesAmount(); i++) {
            if(i < 20)  frames.add(new TextureRegion(getTexture(), x + i * ((MyTextureRegion) textureRegion).getWidth(),y, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
            else
                frames.add(new TextureRegion(getTexture(), x + (i - 20)* ((MyTextureRegion) textureRegion).getWidth(), y + regionHeight, ((MyTextureRegion) textureRegion).getWidth(), ((MyTextureRegion) textureRegion).getHeight()));
        }
        Animation animation = new Animation(frameDuration, frames); //0.1 default (less = faster)

        frames.clear();

        return animation;
    }

    public boolean isAnimationFinished(Animation animation){
        return animation.isAnimationFinished(stateTimer);
    }

    public void update(float delta){
        TextureRegion region = getFrame(delta);

        setRegion(region);

        //flip sprite
        if(!isPlayer1Side)
            flip(true,false);


        setBounds((player_body.getPosition().x - getWidth() / 2), player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM),
                (region.getRegionWidth() * widthScalingFactor)/StreetFighter.PPM,     //scaling
                (region.getRegionHeight() * heightScalingFactor)/StreetFighter.PPM); //scaling

        //if  current animation width and height is different from previous animation - we need to setPosition after setBounds (like setting position before and after scaling )
        setPosition(player_body.getPosition().x - getWidth() / 2, player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM)); //without setPosition() animation lagging

        if(currentState == State.CROUCHING3 || currentState == State.CROUCHING1 || currentState == State.CROUCHING2) {
            setPosition((player_body.getPosition().x - scaledWidht/2), player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM));
        }

        //if jumping - use last body.position.y
        if(currentState != State.JUMPING & currentState != State.JUMPING_FORWARD & currentState != State.JUMPING_BACK){
            lastPositionY = player_body.getPosition().y;
            lastPositionX = player_body.getPosition().x;
        }

        if(currentState == State.JUMPING) {
            setPosition((player_body.getPosition().x - scaledWidht/ 2), lastPositionY - (standing_lowBox_hy/StreetFighter.PPM));
        }

        if(currentState == State.JUMPING_BACK) {
            setPosition((lastPositionX - getWidth() / 4), lastPositionY - (standing_lowBox_hy / StreetFighter.PPM));
        }
        if(currentState == State.JUMPING_FORWARD) {
            setPosition((lastPositionX - getWidth()), lastPositionY - (standing_lowBox_hy / StreetFighter.PPM));
        }



    }

    public TextureRegion getFrame(float delta){
        stateTimer = currentState == previousState ? stateTimer + delta : 0;

        TextureRegion region;
        switch (currentState){
            case STANDING:
               region = (TextureRegion) standingAnimation.getKeyFrame(stateTimer, true);
                break;
            case MOVING_FORWARD:
               region = (TextureRegion) movingRightAnimation.getKeyFrame(stateTimer, true);
                break;
            case MOVING_BACK:
               region = (TextureRegion) movingLeftAnimation.getKeyFrame(stateTimer, true);
                break;
            case CROUCHING1:
                region = (TextureRegion) crouchingAnimation1.getKeyFrame(stateTimer, false);
                break;
            case CROUCHING2:
                region = (TextureRegion) crouchingAnimation2.getKeyFrame(stateTimer, true);
                break;
            case CROUCHING3:
                region = (TextureRegion) crouchingAnimation3.getKeyFrame(stateTimer, false);
                break;
            case JUMPING:
                region = (TextureRegion) jumpingAnimation.getKeyFrame(stateTimer, false);
                break;
            case JUMPING_FORWARD:
                region = (TextureRegion) jumpingLeftAnimation.getKeyFrame(stateTimer, false);
                break;
            case JUMPING_BACK:
                region = (TextureRegion) jumpingRightAnimation.getKeyFrame(stateTimer, false);
                break;

                default :
                    region = (TextureRegion) standingAnimation.getKeyFrame(stateTimer, true);
                    break;

        }


       checkForFixturesChange();

        previousState = currentState;

        return region;
    }

    public void checkForFixturesChange(){
        if (currentState == State.CROUCHING1 & previousState != State.CROUCHING1)
            changeFixturesShape();

        if(((previousState == State.CROUCHING1 & currentState != State.CROUCHING1) || previousState == State.CROUCHING2) &  crouching == false){
            changeFixturesShape();
        }
    }

    public void turnSides(){
        Animation tempAnimation = this.movingLeftAnimation;
        this.movingLeftAnimation = movingRightAnimation;
        movingRightAnimation = tempAnimation;

        tempAnimation = this.jumpingLeftAnimation;
        this.jumpingLeftAnimation = jumpingRightAnimation;
        jumpingRightAnimation = tempAnimation;

        float tempSpeed = walkingForwardSpeed;
        walkingForwardSpeed = -1 * walkingBackSpeed;
        walkingBackSpeed = -1 * tempSpeed;

        tempSpeed = jumpingForwardSpeed;
        jumpingForwardSpeed = -1 * jumpingBackSpeed;
        jumpingBackSpeed = -1 * tempSpeed;

        headTurn = headTurn * -1;
        changeFixturesShape();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

}
