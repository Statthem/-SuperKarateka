package com.mygdx.game.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AnalogStick extends Touchpad implements Disposable {

    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;
    public Stage stage;
    private Viewport viewport;

    public AnalogStick(Batch batch) {
        super(75, getTouchpadStyle());
        setBounds(250f, 200f, 300f, 300f);

        viewport = new StretchViewport(1920, 1080);

        stage = new Stage(viewport, batch);
        stage.addActor(this);
    }

    private static Touchpad.TouchpadStyle getTouchpadStyle() {
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("StreetFighter3_Resources/Sprites/Controls/touchBackground.png"));
        touchpadSkin.add("touchKnob", new Texture("StreetFighter3_Resources/Sprites/Controls/touchBackground.png"));

        touchpadStyle = new Touchpad.TouchpadStyle();

        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        return touchpadStyle;
    }


    @Override
    public void dispose() {
        stage.dispose();
        this.dispose();
    }
}
