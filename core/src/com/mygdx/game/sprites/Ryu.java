package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.StreetFighter;
import com.mygdx.game.extended.MyTextureRegion;
import com.mygdx.game.screens.PlayScreen;

public class Ryu extends Player {

    public Ryu(PlayScreen screen, boolean isPlayer1) {
        super(screen, isPlayer1);

        populateTextureRegionMap();
        setFixtures();
        createPlayer();

        standingAnimation = getAnimation("standing");
        movingRightAnimation = getAnimation("movingRight");
        movingLeftAnimation = getAnimation("movingLeft");
        crouchingAnimation1 = getAnimation("crouching", 0, 3, 0.025f);
        crouchingAnimation2 = getAnimation("crouching", 4,8, 0.2f);
        crouchingAnimation3 = getAnimation("crouching",9,11,0.025f );
        jumpingAnimation = getJumpingAnimation("jumping", 0.02f);

    }

    @Override
    protected void setFixtures() {
        sprite_width = 78;
        sprite_height = 111;

        standing_lowBox_hx = 125;
        standing_lowBox_hy = 80;
        standing_midBox_hx = 90;
        standing_midBox_hy = 85;
        standing_highBox_hx = 105;
        standing_highBox_hy = 55;
        standing_headBox_hx = 33;
        standing_headBox_hy = 28;

        crouching_lowBox_hx = 125;
        crouching_lowBox_hy = 50;
        crouching_midBox_hx = 90;
        crouching_midBox_hy = 40;
        crouching_highBox_hx = 105;
        crouching_highBox_hy = 30;
        crouching_headBox_hx = 33;
        crouching_headBox_hy = 28;

        scaledWidht = (this.sprite_width * this.widthScalingFactor)/StreetFighter.PPM;
    }


    @Override
    protected void populateTextureRegionMap() {
        textureRegionMap.put("standing", new MyTextureRegion(basicAtlas.findRegion("ryu_standing_sprite_sheet"),78,111, 10));
        textureRegionMap.put("crouching", new MyTextureRegion(basicAtlas.findRegion("ryu_crouching_sprite_sheet"),88,109,12));
        textureRegionMap.put("movingRight", new MyTextureRegion(basicAtlas.findRegion("ryu_walking_right_sprite_sheet"),112,113,11));
        textureRegionMap.put("movingLeft", new MyTextureRegion(basicAtlas.findRegion("ryu_walking_left_sprite_sheet"),112,113,11));
        textureRegionMap.put("jumping", new MyTextureRegion(jumpingAtlas.findRegion("ryu_jumping_sprite_sheet"),86,192,34));
    }

    @Override
    protected void createPlayer() {
        super.createPlayer();


    }
    @Override
    public void update(float delta){
        super.update(delta);
    }

    @Override
    public void dispose() {
        screen.manager.unload("StreetFighter3_Resources/Sprites/Ryu/packs/Ryu_basic_pack.atlas");
        screen.manager.unload("StreetFighter3_Resources/Sprites/Ryu/packs/Ryu_Jumping_pack.atlas");
    }
}
