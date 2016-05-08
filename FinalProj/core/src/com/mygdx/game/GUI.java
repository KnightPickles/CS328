package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Created by KnightPickles on 5/7/16.
 */
public class GUI {
    Skin skin;
    Stage stage;

    Window playerWin, levelWin;
    String playerClass = "Wizard";
    int level = 1;
    int wave = 1;
    int waves = 10;
    int gold = 23;
    int levelGold = 100;
    Label levelLabel, waveLabel, wavesLabel, goldLabel, levelGoldLabel;

    GUI(Stage stage) {
        skin = new Skin(Gdx.files.internal("ui/ui-blue.json"), new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack")));
        this.stage = stage;

        guiInit();
    }

    void guiInit() {
        // Map level, wave number, gold remaining, player stats, turret stats

        levelLabel = new Label("Level: " + Integer.toString(level), skin);
        waveLabel = new Label("Wave: " + Integer.toString(wave), skin);
        wavesLabel = new Label("Waves: " + Integer.toString(waves), skin);
        goldLabel = new Label("Gold Left: " + Integer.toString(levelGold), skin);
        levelGoldLabel = new Label("Your Gold: " + Integer.toString(gold), skin);

        Table levelInfo = new Table(skin);
        levelInfo.add(new Label("Difficulty: normal", skin)).pad(3);
        levelInfo.row();
        levelInfo.add(levelLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(waveLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(wavesLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(goldLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(levelGoldLabel).left().pad(3);

        levelWin = new Window("", skin, "no-dialog");
        levelWin.setPosition(0,0);
        levelWin.setWidth(155);
        levelWin.setHeight(170);
        levelWin.setMovable(true);
        levelWin.add(levelInfo);
        levelWin.setPosition(0,0);
        stage.addActor(levelWin);


        Table table = new Table(skin);
        Table header = new Table(skin);
        Table master = new Table(skin);
        master.add(header).left();
        master.row();
        master.add(table).left();

        header.add(new Label("Player Stats", skin)).height(25).width(50);
        header.row();
        header.add(new Label("Class: " + playerClass, skin)).height(25).width(50);

        table.add(new Label("Str", skin)).pad(3).left();
        table.add(new Button(skin, "minus")).width(25).height(25).pad(3);
        table.add(new Label("0", skin)).width(25).height(25).pad(3);
        table.add(new Button(skin, "plus")).width(25).height(25).pad(3);
        table.row();
        table.add(new Label("Dex", skin)).pad(3).left();
        table.add(new Button(skin, "minus")).width(25).height(25).pad(3);
        table.add(new Label("0", skin)).width(25).height(25).pad(3);
        table.add(new Button(skin, "plus")).width(25).height(25).pad(3);
        table.row();
        table.add(new Label("Wis", skin)).pad(3).left();
        table.add(new Button(skin, "minus")).width(25).height(25).pad(3);
        table.add(new Label("0", skin)).width(25).height(25).pad(3);
        table.add(new Button(skin, "plus")).width(25).height(25).pad(3);

        playerWin = new Window("", skin, "no-dialog");
        playerWin.setWidth(155);
        playerWin.setHeight(170);
        playerWin.setMovable(true);
        playerWin.setPosition(Gdx.graphics.getWidth() - master.getWidth(), 0);
        playerWin.add(master);
        stage.addActor(playerWin);
    }

    void update() {
        levelLabel.setText("Level: " + Integer.toString(level));
    }

}
