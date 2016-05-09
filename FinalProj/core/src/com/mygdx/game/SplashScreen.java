package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

/**
 * Created by KnightPickles on 5/8/16.
 */
public class SplashScreen implements Screen {

    Skin skin;
    Stage stage;

    Window background;
    Window menu;
    Window instructions;
    Table gra;

    SplashScreen() {
        skin = new Skin(Gdx.files.internal("ui/ui-blue.json"), new TextureAtlas(Gdx.files.internal("ui/ui-blue.pack")));
        stage = new Stage();

        background = new Window("", skin, "no-dialog");
        menu = new Window("", skin, "no-dialog");
        instructions = new Window("", skin, "no-dialog");


        TextButton start = new TextButton("Start Game", skin);
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainGameClass._instance.setScreen(new GameScreen(MainGameClass._instance));
            }
        });

        TextButton exit = new TextButton("Exit Game", skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        gra = new Table(skin);

        Label l = new Label("GHOST", skin);
        Label m = new Label("TOWER", skin);
        Label n = new Label("DEFENSE", skin);
        l.setFontScale(5);
        m.setFontScale(5);
        n.setFontScale(5);

        Table menuTable = new Table(skin);
        menuTable.add(start).height(35).width(120).pad(3);
        menuTable.row();
        menuTable.add(exit).height(35).width(120).pad(3);
        menu.add(menuTable);

        Table dirs = new Table();
        dirs.add(new Label("Prevent ghosts from stealing back their gold!", skin));
        dirs.row();
        dirs.add(new Label("Right click to attack, WASD to move the player.", skin));
        dirs.row();
        dirs.add(new Label("Switch between classes by upgrading your stats.", skin));
        dirs.row();
        dirs.add(new Label("Switch between turrets by upgrading turret stats.", skin));
        dirs.row();
        dirs.add(new Label("Enter build mode with 'space', and the menu with 'esc'", skin));


        instructions.add(dirs);

        gra.add(l);
        gra.row().center();
        gra.add(m);
        gra.row().center();
        gra.add(n);
        gra.row().center();
        gra.add(menu);
        gra.row().center().pad(15);
        gra.add(instructions);

        background.add(gra).center();
        background.setWidth(Gdx.graphics.getWidth());
        background.setHeight(Gdx.graphics.getHeight());
        stage.addActor(background);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
