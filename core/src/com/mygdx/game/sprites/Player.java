package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.mygdx.game.StreetFighter;
import com.mygdx.game.creators.WorldCreator;
import com.mygdx.game.extended.MyTextureRegion;
import com.mygdx.game.screens.PlayScreen;

import java.util.HashMap;
import java.util.Map;


public abstract class Player extends Sprite {
    public enum State {STANDING, CROUCHING, MOVING_RIGHT, MOVING_LEFT, JUMPING, FALLING, FIGHTING, HITSTUN}
    public State currentState;
    public State previousState;

    protected Array<Animation> playerAnimations;
    protected Map<String, MyTextureRegion> textureRegionMap;
    public TextureAtlas atlas;

    protected MyTextureRegion currentTextureRegion;

    private Animation standingAnimation;
    private Animation jumpingAnimation;
    private Animation movingLeftAnimation;
    private Animation movingRightAnimation;
    private Animation crouchingAnimation;

    private float stateTimer;

    private boolean isPlayer1;
    public boolean crouching;

    private float widthScalingFactor = 4.5f;
    private float heightScalingFactor = 5.6f;

    private static final int ryuSprite_width = 78;
    private static final int ryuSprite_height = 111;

    private static int player_lowBox_hx = 145;
    private static int player_lowBox_hy = 90;

    private static int player_midBox_hx = 95;
    private static int player_midBox_hy = 100;

    private static int player_highBox_hx = 125;
    private static int player_highBox_hy = 55;

    private static int player_headBox_hx = 38;
    private static int player_headBox_hy = 38;

    //private static float ryuSprite_scaleFactor = 1.5f;

    private World world;
    public Body player_body;

    public Player(PlayScreen screen, boolean isPlayer1){
        //super(screen.getAtlas().findRegion("ryu_standing_sprite_sheet"));
        super();
        this.world = WorldCreator.world;

        this.isPlayer1 = isPlayer1;
        crouching = false;

        String simpleClassName = this.getClass().getSimpleName();
        //atlas = new TextureAtlas("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_passive_sprite_pack.atlas");


        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas", TextureAtlas.class);

        screen.manager.finishLoading();
        System.out.println(screen.manager.isLoaded("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas"));


        if(screen.manager.isLoaded("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas"))
        atlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas");

        textureRegionMap = new HashMap<>();
        populateTextureRegionMap();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        currentTextureRegion = textureRegionMap.get("standing");

        standingAnimation = getAnimation("standing");
        //jumpingAnimation = getAnimation("jumping");
        movingRightAnimation = getAnimation("movingRight");
        movingLeftAnimation = getAnimation("movingLeft");
        crouchingAnimation = getAnimation("crouching");

        createPlayer();
    }

    protected abstract void populateTextureRegionMap();

    protected void createPlayer(){
        BodyDef bodyDef = new BodyDef();

        float headTurn = 0.25f;
        if(!isPlayer1) {
            bodyDef.position.set(15.2f, 4.7f); // x, y of body center
            headTurn = -0.25f;
        } else
        bodyDef.position.set(4.2f, 4.7f); // x, y of body center

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        player_body = world.createBody(bodyDef);

        //lower body fixture
        FixtureDef low_fixtureDef = new FixtureDef();
        PolygonShape low_shape = new PolygonShape();
        low_shape.setAsBox(player_lowBox_hx/StreetFighter.PPM,player_lowBox_hy/StreetFighter.PPM);
        low_fixtureDef.shape = low_shape;
        player_body.createFixture(low_fixtureDef);

        //mid body fixture
        FixtureDef mid_fixtureDef = new FixtureDef();
        PolygonShape mid_shape = new PolygonShape();
        mid_shape.setAsBox(player_midBox_hx/StreetFighter.PPM,player_midBox_hy/StreetFighter.PPM,
                new Vector2(0,  player_lowBox_hy/StreetFighter.PPM + player_midBox_hy/StreetFighter.PPM),
                0);
        mid_fixtureDef.shape = mid_shape;
        player_body.createFixture(mid_fixtureDef);

        //high body fixture
        FixtureDef high_fixtureDef = new FixtureDef();
        PolygonShape high_shape = new PolygonShape();
        high_shape.setAsBox(player_highBox_hx/StreetFighter.PPM,player_highBox_hy/StreetFighter.PPM,
                new Vector2(0, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + player_highBox_hy/StreetFighter.PPM)),
                0);
        high_fixtureDef.shape = high_shape;
        player_body.createFixture(high_fixtureDef);

        //head body fixture
        FixtureDef head_fixtureDef = new FixtureDef();
        PolygonShape head_shape = new PolygonShape();
        head_shape.setAsBox(player_headBox_hx/StreetFighter.PPM,player_headBox_hy/StreetFighter.PPM,
                new Vector2(headTurn, (player_lowBox_hy/StreetFighter.PPM + (player_midBox_hy/StreetFighter.PPM * 2) + (player_highBox_hy/StreetFighter.PPM * 2) + player_headBox_hy/StreetFighter.PPM)),
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

            //set position and size for player sprite                               //scaling factor                                                              //scaling factor
            //  setBounds(0,0,(((MyTextureRegion) textureRegion).getWidth() * 4.5f)/StreetFighter.PPM, (((MyTextureRegion) textureRegion).getHeight() * 5.6f)/StreetFighter.PPM); //350, 600

            return animation;
    }

    public void update(float delta){
        //set sprite position on box
        setRegion(getFrame(delta));
        setBounds((player_body.getPosition().x - getWidth() / 2), (player_body.getPosition().y - getHeight()/2) + 2.1f,
                (((MyTextureRegion) currentTextureRegion).getWidth() * widthScalingFactor)/StreetFighter.PPM,     //scaling
                (((MyTextureRegion) currentTextureRegion).getHeight() * heightScalingFactor)/StreetFighter.PPM); //scaling
        //if  current animation width and height is different from previous animation - we need to setPosition after setBounds (like setting position before and after scaling )
        setPosition(player_body.getPosition().x - getWidth() / 2, (player_body.getPosition().y - getHeight()/2) + 2.1f); //without setPosition() animation lagging
    }

    public TextureRegion getFrame(float delta){
        currentState = getState();

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
            case CROUCHING:
                currentTextureRegion = textureRegionMap.get("crouching");
                region = (TextureRegion) crouchingAnimation.getKeyFrame(stateTimer, false);
                break;

                default :
                    currentTextureRegion = textureRegionMap.get("standing");
                    region = (TextureRegion) standingAnimation.getKeyFrame(stateTimer, true);
                    break;
        }
         // rotate sprite on x
        //region.flip(true,false);
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return region;
    }

    public State getState(){
        if(player_body.getLinearVelocity().y > 0)
            return State.JUMPING;
       else if(player_body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(crouching == true)
            return State.CROUCHING;
        else if(player_body.getLinearVelocity().x > 0)
            return State.MOVING_RIGHT;
        else if(player_body.getLinearVelocity().x < 0)
            return State.MOVING_LEFT;
        else return State.STANDING;
    }

}
