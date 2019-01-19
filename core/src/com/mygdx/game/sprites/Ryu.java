package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.extended.MyTextureRegion;
import com.mygdx.game.screens.PlayScreen;

public class Ryu extends Player {

    private static final int ryuSprite_width = 78;
    private static final int ryuSprite_height = 111;


    private static int player_lowBox_hx = 145;
    private static int player_lowBox_hy = 90;

    private static int player_midBox_hx = 95;
    private static int player_midBox_hy = 100;

    private static int player_highBox_hx = 125;
    private static int player_highBox_hy = 55;

    private static int player_headBox_hx = 38;
    private static int player_headBox_hy = 35;


    public Ryu(PlayScreen screen, boolean isPlayer1) {
        super(screen, isPlayer1);

        String simpleClassName = this.getClass().getSimpleName();
        //atlas = new TextureAtlas("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_passive_sprite_pack.atlas");
        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas", TextureAtlas.class);
        screen.manager.load("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_Jumping_pack.atlas", TextureAtlas.class);
        screen.manager.finishLoading();

        basicAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_basic_pack.atlas");
        jumpingAtlas = screen.manager.get("StreetFighter3_Resources/Sprites/" + simpleClassName + "/packs/" + simpleClassName + "_Jumping_pack.atlas");

        populateTextureRegionMap();

        standingAnimation = getAnimation("standing");
        movingRightAnimation = getAnimation("movingRight");
        movingLeftAnimation = getAnimation("movingLeft");
        crouchingAnimation1 = getAnimation("crouching", 0, 3, 0.025f);
        crouchingAnimation2 = getAnimation("crouching", 4,8, 0.2f);
        crouchingAnimation3 = getAnimation("crouching",9,11,0.025f );
        jumpingAnimation = getJumpingAnimation("jumping", 0.02f);

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
