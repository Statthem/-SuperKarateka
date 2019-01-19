package com.mygdx.game.sprites;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.creators.WorldCreator;
import com.mygdx.game.extended.MyTextureRegion;
import com.mygdx.game.screens.PlayScreen;

import java.util.HashMap;
import java.util.Map;


public abstract class Player extends Sprite implements Disposable{
    public enum State {STANDING, CROUCHING1, CROUCHING2, CROUCHING3, MOVING_RIGHT, MOVING_LEFT, JUMPING, FALLING, FIGHTING, HITSTUN}
    public State currentState;
    public State previousState;

    PlayScreen screen;

    protected Array<Animation> playerAnimations;
    protected Map<String, MyTextureRegion> textureRegionMap;
    public TextureAtlas basicAtlas;
    public TextureAtlas jumpingAtlas;

    protected MyTextureRegion currentTextureRegion;

    public Animation standingAnimation;
    public Animation movingLeftAnimation;
    public Animation movingRightAnimation;
    public Animation crouchingAnimation1;
    public Animation crouchingAnimation2;
    public Animation crouchingAnimation3;
    public Animation jumpingAnimation;


    private float stateTimer;

    private boolean isPlayer1;
    public boolean crouching;
    public boolean jumping;

//    private float widthScalingFactor = 4.5f;
//    private float heightScalingFactor = 5.6f;

    private float widthScalingFactor = 3.58f; //343(4.4)  //280(3.58)
    private float heightScalingFactor = 4.5f; //610.5(5.5)  //500(4.5) // 610:343 = 1.778 //4.4 // 3.52

    private static final int ryuSprite_width = 78;
    private static final int ryuSprite_height = 111;

    protected int standing_lowBox_hx = 145;
    protected int standing_lowBox_hy = 90;
    protected int standing_midBox_hx = 95;
    protected int standing_midBox_hy = 100;
    protected int standing_highBox_hx = 125;
    protected int standing_highBox_hy = 55;
    protected int standing_headBox_hx = 38;
    protected int standing_headBox_hy = 38;

    protected float scaledWidht = (ryuSprite_width * widthScalingFactor)/StreetFighter.PPM;

    private float bodyHeight = (standing_lowBox_hy*2 + standing_midBox_hy*2 + +standing_highBox_hy*2 + standing_headBox_hy*2)/StreetFighter.PPM;

    //private static float ryuSprite_scaleFactor = 1.5f;

    private World world;
    public Body player_body;


    public Player(PlayScreen screen, boolean isPlayer1){
        //super(screen.getAtlas().findRegion("ryu_standing_sprite_sheet"));
        super();
        this.screen = screen;
        this.world = WorldCreator.world;

        this.isPlayer1 = isPlayer1;
        crouching = false;
        jumping = false;

        textureRegionMap = new HashMap<>();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        createPlayer();
    }

    protected abstract void populateTextureRegionMap();

    protected void createPlayer(){
        BodyDef bodyDef = new BodyDef();

        float headTurn = 0.25f;
        if(!isPlayer1) {
            bodyDef.position.set(15.2f, 2.1f); // x, y of body center
            headTurn = -0.25f;
        } else
        bodyDef.position.set(4.2f, 2.1f); // x, y of body center

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player_body = world.createBody(bodyDef);

        //lower body fixture
        FixtureDef low_fixtureDef = new FixtureDef();
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(standing_lowBox_hx /StreetFighter.PPM, standing_lowBox_hy /StreetFighter.PPM, new Vector2(0, 0),0);
        low_fixtureDef.shape = low_shape;
        player_body.createFixture(low_fixtureDef);

        //mid body fixture
        FixtureDef mid_fixtureDef = new FixtureDef();
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(standing_midBox_hx /StreetFighter.PPM, standing_midBox_hy /StreetFighter.PPM,
                new Vector2(0,  standing_lowBox_hy /StreetFighter.PPM + standing_midBox_hy /StreetFighter.PPM),
                0);
        mid_fixtureDef.shape = mid_shape;
        player_body.createFixture(mid_fixtureDef);

        //high body fixture
        FixtureDef high_fixtureDef = new FixtureDef();
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(standing_highBox_hx /StreetFighter.PPM, standing_highBox_hy /StreetFighter.PPM,
                new Vector2(0, (standing_lowBox_hy /StreetFighter.PPM + (standing_midBox_hy /StreetFighter.PPM * 2) + standing_highBox_hy /StreetFighter.PPM)),
                0);
        high_fixtureDef.shape = high_shape;
        player_body.createFixture(high_fixtureDef);

        //head body fixture
        FixtureDef head_fixtureDef = new FixtureDef();
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(standing_headBox_hx /StreetFighter.PPM, standing_headBox_hy /StreetFighter.PPM,
                new Vector2(headTurn, (standing_lowBox_hy /StreetFighter.PPM + (standing_midBox_hy /StreetFighter.PPM * 2) + (standing_highBox_hy /StreetFighter.PPM * 2) + standing_headBox_hy /StreetFighter.PPM)),
                0);
        head_fixtureDef.shape = head_shape;
        player_body.createFixture(head_fixtureDef);

        player_body.setUserData("standing");
    }

    public Animation getAnimation(String key){
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
            Animation animation = new Animation(0.065f, frames); //0.1 default (less = faster)
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


    public void update(float delta){
        setRegion(getFrame(delta));
        setBounds((player_body.getPosition().x - getWidth() / 2), player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM),
                (((MyTextureRegion) currentTextureRegion).getWidth() * widthScalingFactor)/StreetFighter.PPM,     //scaling
                (((MyTextureRegion) currentTextureRegion).getHeight() * heightScalingFactor)/StreetFighter.PPM); //scaling
        //if  current animation width and height is different from previous animation - we need to setPosition after setBounds (like setting position before and after scaling )
       // setPosition(player_body.getPosition().x - getWidth() / 2, (player_body.getPosition().y - getHeight()/2)); //without setPosition() animation lagging
        setPosition(player_body.getPosition().x - getWidth() / 2, player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM)); //without setPosition() animation lagging

         if(currentState == State.CROUCHING3 || currentState == State.CROUCHING1 || currentState == State.CROUCHING2) {
             setPosition((player_body.getPosition().x - scaledWidht/ 2), player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM));
         }

         if(currentState == State.JUMPING) {
             setPosition((player_body.getPosition().x - scaledWidht/ 2), player_body.getPosition().y - (standing_lowBox_hy/StreetFighter.PPM)); //without setPosition() animation lagging);
         }
    }

    public TextureRegion getFrame(float delta){
        currentState = getState();
        stateTimer = currentState == previousState ? stateTimer + delta : 0;

        TextureRegion region;
        switch (currentState){
            case STANDING:
                currentTextureRegion = textureRegionMap.get("standing");
               region = (TextureRegion) standingAnimation.getKeyFrame(stateTimer, true);
                break;
            case MOVING_RIGHT:
                currentTextureRegion = textureRegionMap.get("movingRight");
               region = (TextureRegion) movingRightAnimation.getKeyFrame(stateTimer, true);
                break;
            case MOVING_LEFT:
                currentTextureRegion = textureRegionMap.get("movingLeft");
               region = (TextureRegion) movingLeftAnimation.getKeyFrame(stateTimer, true);
                break;
            case CROUCHING1:
                currentTextureRegion = textureRegionMap.get("crouching");
                region = (TextureRegion) crouchingAnimation1.getKeyFrame(stateTimer, false);
                break;
            case CROUCHING2:
                currentTextureRegion = textureRegionMap.get("crouching");
                region = (TextureRegion) crouchingAnimation2.getKeyFrame(stateTimer, true);
                break;
            case CROUCHING3:
                currentTextureRegion = textureRegionMap.get("crouching");
                region = (TextureRegion) crouchingAnimation3.getKeyFrame(stateTimer, false);
                break;
            case JUMPING:
                currentTextureRegion = textureRegionMap.get("jumping");
                region = (TextureRegion) jumpingAnimation.getKeyFrame(stateTimer, false);
                break;

                default :
                    currentTextureRegion = textureRegionMap.get("standing");
                    region = (TextureRegion) standingAnimation.getKeyFrame(stateTimer, true);
                    break;
        }
         // rotate sprite on x
        //region.flip(true,false);
        previousState = currentState;

        return region;
    }

    public State getState() {
        State state = State.STANDING;

        if(previousState == State.JUMPING & jumpingAnimation.isAnimationFinished(stateTimer))
        {
            jumping = false;
        }

        if (player_body.getLinearVelocity().y < 0)
            state = State.FALLING;
        if (player_body.getLinearVelocity().x > 0)
            state = State.MOVING_RIGHT;
         if (player_body.getLinearVelocity().x < 0)
            state = State.MOVING_LEFT;
         if (crouching == true) {
            if ((previousState == State.STANDING || previousState == State.CROUCHING1) && previousState != State.CROUCHING2) {
                state = State.CROUCHING1;
            }                                                                        // F-ing f-ck
            if ((previousState == State.CROUCHING1 & state == State.CROUCHING1 & crouchingAnimation1.isAnimationFinished(stateTimer)) || previousState == State.CROUCHING2){
                state = State.CROUCHING2;
            }
        }
        if(crouching == false ){
            if(previousState == State.CROUCHING1 & !crouchingAnimation1.isAnimationFinished(stateTimer)) {
                state = State.CROUCHING1;
            }
            if(previousState == State.CROUCHING1 & crouchingAnimation1.isAnimationFinished(stateTimer)) {
                state = State.CROUCHING3;
             //  stateTimer = 0;
            }
             if((previousState == State.CROUCHING1 & crouchingAnimation1.isAnimationFinished(stateTimer)) ||  previousState == State.CROUCHING2 || previousState == State.CROUCHING3) {
                 state = State.CROUCHING3;
             }

             //if(first starting crouching3 animation - set stateTimer = 0(make is go from the first frame))
             if(previousState == State.CROUCHING2) {
             //    stateTimer = 0;
             }
            if(previousState == State.CROUCHING3 & crouchingAnimation3.isAnimationFinished(stateTimer)){
                state = State.STANDING;
              //  stateTimer = 0;
            }
        }

        if(jumping == true){
             state = State.JUMPING;
        }

        return state;
    }

}
