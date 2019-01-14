package com.mygdx.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer fightTimer;
    private Integer score;

    Label fighTiimerLabel;
    Label scoreLabel;
    Label player1Label;
    Label player2Label;

    public HUD(SpriteBatch batch){
        fightTimer = 90;
        score = 0;

        viewport = new StretchViewport(800, 480);
        stage = new Stage(viewport, batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        fighTiimerLabel = new Label("TIME:" + String.format("%02d", fightTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel =  new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player1Label = new Label("Player 1",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player2Label = new Label("Player 2",new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(player1Label).expandX().padTop(10).padLeft(20);
        table.add(player2Label).expandX().padTop(10).padRight(20);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(fighTiimerLabel).expand();

        stage.addActor(table);

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
