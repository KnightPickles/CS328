package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by KnightPickles on 5/7/16.
 */
public class GUI {
    Skin skin;
    Stage stage;

    Window playerWin, levelWin;
    int level = 1;
    int wave = 1;
    int waves = 10;
    int gold = 23;
    int levelGold = 100;
    Label levelLabel, waveLabel, wavesLabel, goldLabel, levelGoldLabel, playerClass, playerStr, playerDex, playerWis, towerStr, towerDex, towerWis;
    Button rPlus, rMinus, gPlus, gMinus, bPlus, bMinus;

    GUI(Stage stage) {
        skin = new Skin(Gdx.files.internal("ui/ui-blue.json"), new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack")));
        this.stage = stage;

        guiInit();
    }

    void guiInit() {
        // Map level, wave number, gold remaining, player stats, turret stats

        // Maybe switch between classes/two of each turret if not enough time.

        levelLabel = new Label("Level: " + Integer.toString(level), skin);
        waveLabel = new Label("Wave: " + Integer.toString(wave), skin);
        wavesLabel = new Label("Waves: " + Integer.toString(waves), skin);
        levelGoldLabel = new Label("Gold Left: " + Integer.toString(levelGold), skin);
        goldLabel = new Label("Your Gold: " + Integer.toString(gold), skin);

        int r = EntityManager._instance.player.redUpgradeLevel;
        int g = EntityManager._instance.player.greenUpgradeLevel;
        int b = EntityManager._instance.player.blueUpgradeLevel;

        playerStr = new Label(Integer.toString(r), skin);
        playerDex = new Label(Integer.toString(g), skin);
        playerWis = new Label(Integer.toString(b), skin);
        if(r > b && r > g) {
            playerClass = new Label("Class: Warrior", skin);
        } else if(g > r && g > b) {
            playerClass = new Label("Class: Ranger", skin);
        } else {
            playerClass = new Label("Class: Wizard", skin);
        }

        Table levelInfo = new Table(skin);
        levelInfo.add(new Label("Difficulty: normal", skin)).pad(3);
        levelInfo.row();
        levelInfo.add(levelLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(waveLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(wavesLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(levelGoldLabel).left().pad(3);
        levelInfo.row();
        levelInfo.add(goldLabel).left().pad(3);

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
        header.add(playerClass).height(25).width(50);

        rPlus = new Button(skin, "plus");
        gPlus = new Button(skin, "plus");
        bPlus = new Button(skin, "plus");

        rMinus = new Button(skin, "minus");
        gMinus = new Button(skin, "minus");
        bMinus = new Button(skin, "minus");

        rPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.upgradeRedLevel();
            }
        });

        gPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.upgradeGreenLevel();
            }
        });

        bPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.upgradeBlueLevel();
            }
        });

        rMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.downgradeRedLevel();
            }
        });

        gMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.downgradeGreenLevel();
            }
        });

        bMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.player.downgradeBlueLevel();
            }
        });

        table.add(new Label("Str", skin)).pad(3).left();
        table.add(rMinus).width(25).height(25).pad(3);
        table.add(playerStr).width(25).height(25).pad(3);
        table.add(rPlus).width(25).height(25).pad(3);
        table.row();
        table.add(new Label("Dex", skin)).pad(3).left();
        table.add(gMinus).width(25).height(25).pad(3);
        table.add(playerDex).width(25).height(25).pad(3);
        table.add(gPlus).width(25).height(25).pad(3);
        table.row();
        table.add(new Label("Wis", skin)).pad(3).left();
        table.add(bMinus).width(25).height(25).pad(3);
        table.add(playerWis).width(25).height(25).pad(3);
        table.add(bPlus).width(25).height(25).pad(3);

        playerWin = new Window("", skin, "no-dialog");
        playerWin.setWidth(155);
        playerWin.setHeight(170);
        playerWin.setMovable(true);
        playerWin.setPosition(Gdx.graphics.getWidth() - master.getWidth(), 0);
        playerWin.add(master);
        stage.addActor(playerWin);
    }

    void update() {
        levelLabel.setText("Level: " + Integer.toString(LevelManager._instance.level + 1));
        waveLabel.setText("Wave: " + Integer.toString(LevelManager._instance.wave));
        wavesLabel.setText("Wave: " + Integer.toString(LevelManager._instance.numWaves));
        levelGoldLabel.setText("Gold Left: " + Integer.toString(Map._instance.levelGold));
        goldLabel.setText("Your Gold: " + Integer.toString(Player.gold));

        int r = EntityManager._instance.player.redUpgradeLevel;
        int g = EntityManager._instance.player.greenUpgradeLevel;
        int b = EntityManager._instance.player.blueUpgradeLevel;

        playerStr.setText(Integer.toString(r));
        playerDex.setText(Integer.toString(g));
        playerWis.setText(Integer.toString(b));

        if(r > b && r > g) {
            playerClass.setText("Class: Warrior");
        } else if(g > r && g > b) {
            playerClass.setText("Class: Ranger");
        } else {
            playerClass.setText("Class: Wizard");
        }
    }

}
