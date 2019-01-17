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
    }

    @Override
    protected void populateTextureRegionMap() {
        textureRegionMap.put("standing", new MyTextureRegion(atlas.findRegion("ryu_standing_sprite_sheet"),78,111, 10));
        textureRegionMap.put("crouching", new MyTextureRegion(atlas.findRegion("ryu_crouching_sprite_sheet"),88,109,12));
        textureRegionMap.put("movingRight", new MyTextureRegion(atlas.findRegion("ryu_walking_right_sprite_sheet"),112,113,11));
        textureRegionMap.put("movingLeft", new MyTextureRegion(atlas.findRegion("ryu_walking_left_sprite_sheet"),112,113,11));
     //   textureRegionMap.put("jumping", new MyTextureRegion(atlas.findRegion("ryu_jumping_sprite_sheet"),86,192,34));
    }

    @Override
    protected void createPlayer() {
        super.createPlayer();


    }
    @Override
    public void update(float delta){
        super.update(delta);
    }
}
