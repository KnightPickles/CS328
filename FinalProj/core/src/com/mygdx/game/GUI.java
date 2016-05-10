package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.awt.*;
import java.util.logging.Level;

/**
 * Created by KnightPickles on 5/7/16.
 */
public class GUI {

    static GUI _instance;
    Skin skin;
    Stage stage;

    Window playerWin, levelWin, towerWin, promptWin, menuWin, endConditionWin, nextLevWin;
    int level = 1;
    int wave = 1;
    int waves = 10;
    int gold = 23;
    int levelGold = 100;
    boolean nextLev = false;
    Label levelLabel, waveLabel, wavesLabel, goldLabel, levelGoldLabel, difficultyLabel, playerClass, playerStr, playerDex, playerWis, turretType, tRed, tGreen, tBlue, endConditionLabel;
    static Label[] prompts = new Label[4];
    static long[] promptStamp = new long[4];
    Button rPlus, rMinus, gPlus, gMinus, bPlus, bMinus, tRPlus, tRMinus, tGPlus, tGMinus, tBPlus, tBMinus;

    int r = EntityManager._instance.player.redUpgradeLevel;
    int g = EntityManager._instance.player.greenUpgradeLevel;
    int b = EntityManager._instance.player.blueUpgradeLevel;
    int statTot = r + g + b;

    double costAccumulator = 1.1;
    int upgradeCost = 15;

    Turret selectedTurret = null;
    Sprite debug = MainGameClass._instance.atlas.createSprite("red_indicator");

    GUI(Stage stage) {
        _instance = this;

        skin = new Skin(Gdx.files.internal("ui/ui-blue.json"), new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack")));
        this.stage = stage;

        debug.setAlpha(0.5f);

        prompts = new Label[4];
        promptStamp = new long[4];

        nextLev = false;
        guiInit();
    }

    void guiInit() {
        // Map level, wave number, gold remaining, player stats, turret stats

        // Maybe switch between classes/two of each turret if not enough time.

        if(SelectionManager._instance.selected != null && SelectionManager._instance.selected.getClass() == Turret.class)
            selectedTurret = (Turret)SelectionManager._instance.selected;
        else selectedTurret = null;

        difficultyLabel = new Label("",skin);
        levelLabel = new Label("Level: " + Integer.toString(level), skin);
        waveLabel = new Label("Wave: " + Integer.toString(wave), skin);
        wavesLabel = new Label("Waves: " + Integer.toString(waves), skin);
        levelGoldLabel = new Label("Gold Left: " + Integer.toString(levelGold), skin);
        goldLabel = new Label("Your Gold: " + Integer.toString(gold), skin);

        Table levelInfo = new Table(skin);
        levelInfo.add(difficultyLabel).left().pad(3);
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
        levelWin.setPosition(0,0);
        levelWin.add(levelInfo);
        stage.addActor(levelWin);

        Table table = new Table(skin);
        Table header = new Table(skin);
        Table master = new Table(skin);
        master.add(header).left();
        master.row();
        master.add(table).left();

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
                if(upgradeCost <= Player.gold) {
                    prompt("Upgraded str.");
                    Player.gold -= upgradeCost;
                    EntityManager._instance.player.upgradeRedLevel();
                    upgradeCost *= costAccumulator;
                } else prompt("Not enough gold to upgrade str. Costs " + upgradeCost);
            }
        });

        gPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradeCost <= Player.gold) {
                    prompt("Upgraded dex.");
                    Player.gold -= upgradeCost;
                    EntityManager._instance.player.upgradeGreenLevel();
                    upgradeCost *= costAccumulator;
                } else prompt("Not enough gold to upgrade dex. Costs " + upgradeCost);
            }
        });

        bPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradeCost <= Player.gold) {
                    prompt("Upgraded wis.");
                    Player.gold -= upgradeCost;
                    EntityManager._instance.player.upgradeBlueLevel();
                    upgradeCost *= costAccumulator;
                } else prompt("Not enough gold to upgrade wis. Costs " + upgradeCost);
            }
        });

        rMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(EntityManager._instance.player.redUpgradeLevel <= 0) {
                    prompt("Cannot downgrade below 0.");
                    return;
                }
                EntityManager._instance.player.downgradeRedLevel();
                Player.gold += 10;
                prompt("Downgraded str for 10 gold.");
            }
        });

        gMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(EntityManager._instance.player.greenUpgradeLevel <= 0) {
                    prompt("Cannot downgrade below 0.");
                    return;
                }
                EntityManager._instance.player.downgradeRedLevel();
                Player.gold += 10;
                prompt("Downgraded dex for 10 gold.");
            }
        });

        bMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(EntityManager._instance.player.blueUpgradeLevel <= 0) {
                    prompt("Cannot downgrade below 0.");
                    return;
                }
                EntityManager._instance.player.downgradeBlueLevel();
                Player.gold += 10;
                prompt("Downgraded wis for 10 gold.");
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

        Table towerTable = new Table(skin);

        turretType = new Label("", skin);

        Table towerHdr = new Table(skin);
        towerHdr.add(new Label("Tower Stats", skin)).pad(3).left();
        towerHdr.row();
        towerHdr.add(turretType).pad(3).left();

        tRPlus = new Button(skin, "plus");
        tGPlus = new Button(skin, "plus");
        tBPlus = new Button(skin, "plus");

        tRMinus = new Button(skin, "minus");
        tGMinus = new Button(skin, "minus");
        tBMinus = new Button(skin, "minus");

        tRPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedTurret == null) return;
                if(selectedTurret.upgradeCost <= Player.gold) {
                    prompt("Upgraded turret str.");
                    Player.gold -= selectedTurret.upgradeCost;
                    selectedTurret.upgradeRedLevel();
                    selectedTurret.upgradeCost *= selectedTurret.costAccumulator;
                } else prompt("Not enough gold to upgrade str. Costs " + selectedTurret.upgradeCost);
            }
        });

        tGPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedTurret == null) return;
                if(selectedTurret.upgradeCost <= Player.gold) {
                    prompt("Upgraded turret dex.");
                    Player.gold -= selectedTurret.upgradeCost;
                    selectedTurret.upgradeGreenLevel();
                    selectedTurret.upgradeCost *= selectedTurret.costAccumulator;
                } else prompt("Not enough gold to upgrade dex. Costs " + selectedTurret.upgradeCost);
            }
        });

        tBPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedTurret == null) return;
                if(selectedTurret.upgradeCost <= Player.gold) {
                    prompt("Upgraded turret wis.");
                    Player.gold -= selectedTurret.upgradeCost;
                    selectedTurret.upgradeBlueLevel();
                    selectedTurret.upgradeCost *= selectedTurret.costAccumulator;
                } else prompt("Not enough gold to upgrade wis. Costs " + selectedTurret.upgradeCost);
            }
        });

        tRed = new Label("", skin);
        tBlue = new Label("", skin);
        tGreen = new Label("", skin);

        Table towerUpd = new Table(skin);
        towerUpd.add(new Label("Red ", skin)).pad(3).left();
        towerUpd.add(tRed).width(25).height(25).pad(3);
        towerUpd.add(tRPlus).width(25).height(25).pad(3);
        towerUpd.row();
        towerUpd.add(new Label("Grn ", skin)).pad(3).left();
        towerUpd.add(tGreen).width(25).height(25).pad(3);
        towerUpd.add(tGPlus).width(25).height(25).pad(3);
        towerUpd.row();
        towerUpd.add(new Label("Blu ", skin)).pad(3).left();
        towerUpd.add(tBlue).width(25).height(25).pad(3);
        towerUpd.add(tBPlus).width(25).height(25).pad(3);

        towerTable.add(towerHdr).left();
        towerTable.row();
        towerTable.add(towerUpd).left();

        towerWin = new Window("", skin, "no-dialog");
        towerWin.setWidth(155);
        towerWin.setHeight(170);
        towerWin.setMovable(true);
        towerWin.setPosition(Gdx.graphics.getWidth() - towerWin.getWidth() - playerWin.getWidth(), 0);
        towerWin.add(towerTable);
        towerWin.left();
        stage.addActor(towerWin);


        Table promptTable = new Table(skin);
        for(int i = 0; i < 4; i++) {
            prompts[i] = new Label("", skin);
            promptTable.add(prompts[i]).left().pad(3);
            promptTable.row();
            promptStamp[i] = System.currentTimeMillis();
        }

        promptWin = new Window("", skin, "no-dialog");
        promptWin.setWidth(400);
        promptWin.setHeight(110);
        promptWin.setMovable(true);
        promptWin.setPosition(levelWin.getWidth(), 0);
        promptWin.add(promptTable);
        promptWin.left();
        stage.addActor(promptWin);

        endConditionLabel = new Label("GAME END", skin);
        endConditionLabel.setFontScale(3);

        Label l = new Label("", skin);
        l.setFontScale(10);

        Table endTable = new Table(skin);
        endTable.add(endConditionLabel);
        endTable.row();
        endTable.add(l);


        endConditionWin = new Window("", skin, "no-dialog");
        endConditionWin.setWidth(300);
        endConditionWin.setHeight(300);
        endConditionWin.setPosition(Gdx.graphics.getWidth() / 2 - endConditionWin.getWidth() / 2, Gdx.graphics.getHeight() / 2 - endConditionWin.getHeight() / 2 + 30);
        endConditionWin.add(endTable);
        endConditionWin.setVisible(false);
        stage.addActor(endConditionWin);


        TextButton exit = new TextButton("Title Screen", skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen._instance.cleanup();
                MainGameClass._instance.setScreen(new SplashScreen());
            }
        });

        TextButton ret = new TextButton("Continue", skin);
        ret.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuWin.setVisible(!menuWin.isVisible());
            }
        });

        Table menuTable = new Table(skin);
        menuTable.add(new Label("Menu",skin)).center().pad(8);
        menuTable.row();
        menuTable.add(exit).height(25).width(100).pad(3);
        menuTable.row();
        menuTable.add(ret).height(25).width(100).pad(3);

        menuWin = new Window("", skin, "no-dialog");
        menuWin.setWidth(130);
        menuWin.setHeight(120);
        menuWin.setMovable(false);
        menuWin.setPosition(Gdx.graphics.getWidth() / 2 - menuWin.getWidth() / 2, Gdx.graphics.getHeight() / 2 - menuWin.getHeight() / 2);
        menuWin.add(menuTable);
        menuWin.setVisible(false);
        stage.addActor(menuWin);

        TextButton nextLevel = new TextButton("Next Level", skin);
        nextLevel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextLevWin.setVisible(false);
                nextLev = true;
            }
        });

        TextButton ex = new TextButton("Title Screen", skin);
        ex.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen._instance.cleanup();
                MainGameClass._instance.setScreen(new SplashScreen());
            }
        });

        Table levTable = new Table(skin);
        Label x = new Label("Level Complete!", skin);
        x.setFontScale(2);
        levTable.add(x).center().pad(8);
        levTable.row();
        levTable.add(nextLevel).height(30).width(120).pad(3);
        levTable.row();
        levTable.add(ex).height(30).width(120).pad(3);

        nextLevWin = new Window("", skin, "no-dialog");
        nextLevWin.setWidth(300);
        nextLevWin.setHeight(200);
        nextLevWin.setMovable(false);
        nextLevWin.setPosition(Gdx.graphics.getWidth() / 2 - nextLevWin.getWidth() / 2, Gdx.graphics.getHeight() / 2 - nextLevWin.getHeight() / 2);
        nextLevWin.add(levTable);
        nextLevWin.setVisible(false);
        stage.addActor(nextLevWin);

    }

    void levelComplete() {
        System.out.println(LevelManager._instance.wave);
        if(LevelManager._instance.wave >= LevelManager._instance.numWaves) {
            nextLev = true;
        } else {
            nextLev = false;
            nextLevWin.setVisible(true);
        }
    }

    boolean nextLevel() {
        return nextLev;
    }

    void endCondition() {
        endConditionWin.setVisible(true);
        menuWin.setVisible(true);

        if(GameScreen._instance.state == GameScreen.State.Defeat)
            endConditionLabel.setText("DEFEAT");
        else if(GameScreen._instance.state == GameScreen.State.Victory)
            endConditionLabel.setText("VICTORY");

    }

    void draw() {
        if(EntityManager._instance.turrets.size() == 0) return;
        if(EntityManager._instance.turrets.size() == 1) debug.setPosition(EntityManager._instance.turrets.get(0).sprite.getX(),EntityManager._instance.turrets.get(0).sprite.getY());

        MainGameClass._instance.batch.begin();
        debug.draw(MainGameClass._instance.batch);
        MainGameClass._instance.batch.end();
    }

    void update() {

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && GameScreen._instance.state != GameScreen.State.Defeat && GameScreen._instance.state != GameScreen.State.Victory)
            menuWin.setVisible(!menuWin.isVisible());

        if(menuWin.isVisible()) {
            GameScreen._instance.state = GameScreen.State.Pause;
        } else GameScreen._instance.state = GameScreen.State.Play;

        if(SelectionManager._instance.selected != null) { // no class
            selectedTurret = (Turret) SelectionManager._instance.selected;
            debug.setPosition(selectedTurret.sprite.getX(), selectedTurret.sprite.getY());
        }

        if(selectedTurret != null) {
            switch(selectedTurret.turretType) {
                case Red: turretType.setText("Type: Red"); break;
                case Green: turretType.setText("Type: Green"); break;
                case Blue: turretType.setText("Type: Blue"); break;
            }

            tRed.setText(Integer.toString(selectedTurret.redUpgradeLevel));
            tGreen.setText(Integer.toString(selectedTurret.greenUpgradeLevel));
            tBlue.setText(Integer.toString(selectedTurret.blueUpgradeLevel));
        } else {
            turretType.setText("None Selected");
            tRed.setText("N/A");
            tGreen.setText("N/A");
            tBlue.setText("N/A");
        }

        //towerWin.setVisible(BuildManager._instance.inBuildMode);

        statTot = r + g + b;
        r = EntityManager._instance.player.redUpgradeLevel;
        g = EntityManager._instance.player.greenUpgradeLevel;
        b = EntityManager._instance.player.blueUpgradeLevel;

        int remainingGold = (int)(Map._instance.totalLevelGold * (1 - Map._instance.minGoldRemaining) + 1) - (Map._instance.totalLevelGold - Map._instance.levelGold);
        if(remainingGold < 0) remainingGold = 0;

        String difficulty;
        switch(LevelManager._instance.difficulty) {
            case EASY: difficulty = "Easy"; break;
            case MEDIOCRE: difficulty = "Mediocre"; break;
            case NORMAL: difficulty = "Normal"; break;
            case HARD: difficulty = "Hard"; break;
            default: difficulty = "";
        }

        difficultyLabel.setText("Difficulty: " + difficulty);
        levelLabel.setText("Level: " + Integer.toString(LevelManager._instance.level + 1));
        waveLabel.setText("Wave: " + Integer.toString(LevelManager._instance.wave));
        wavesLabel.setText("Waves: " + Integer.toString(LevelManager._instance.numWaves));
        levelGoldLabel.setText("Gold Left: " + Integer.toString(remainingGold));
        goldLabel.setText("Your Gold: " + Integer.toString(Player.gold));

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

        // Fading some prompts like a boss
        long time = System.currentTimeMillis();
        for(int i = 0; i < 3; i++) {
            long deltaTime = time - promptStamp[i];
            if (deltaTime > 1000) {
                Color c = prompts[i].getColor();
                if(deltaTime > 3000) {
                    c.a = 0;
                } else c.a = 1f - (deltaTime - 2000) / 1000f;
                prompts[i].setColor(c);
            }
        }
    }

    static void prompt(String s) {
        if(prompts[0] == null) return;
        for(int i = 0; i < 3; i++) {
            prompts[i].setText(prompts[i + 1].getText());
            prompts[i].setColor(prompts[i + 1].getColor());
            promptStamp[i] = promptStamp[i + 1];
        }
        prompts[3].setText(s);
        Color c = prompts[3].getColor();
        c.a = 1;
        prompts[3].setColor(c);
        promptStamp[3] = System.currentTimeMillis();
    }

}
