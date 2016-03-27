package edu.cs328;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.awt.*;

/**
 * Created by KnightPickles on 3/26/16.
 */
public class SplashScreen implements Screen {

    private HW4 game;
    private Stage stage;
    private Skin skin;
    private OrthographicCamera camera;
    private int resInd = 2;

    public SplashScreen(HW4 game) {
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth() , Gdx.graphics.getHeight(), camera));
        Gdx.input.setInputProcessor(stage);
        mainMenu();
    }

    private Vector2 getRes(SelectBox b) {
        String[] split = ((String)b.getSelected()).split(" ");
        if(split != null) {
            return new Vector2(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } else return null;
    }

    public void options() {
        final SelectBox resolutions = new SelectBox(skin);
        resolutions.setHeight(20f);
        resolutions.setWidth(100f);
        resolutions.setItems(new String[] {"1024 576", "1152 648", "1280 720", "1366 768", "1600 900", "1920 1080"});
        resolutions.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 70f);
        resolutions.setSelectedIndex(resInd);

        CheckBox windowed = new CheckBox(" Windowed", skin);
        windowed.setPosition(Gdx.graphics.getWidth() /2 + 10, Gdx.graphics.getHeight()/2 - 72f);
        if(windowed.isChecked()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        final TextButton back = new TextButton("Return", skin, "default");
        back.setWidth(200f);
        back.setHeight(20f);
        back.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mainMenu();
            }
        });

        final TextButton setRes = new TextButton("Set Resolution", skin, "default");
        setRes.setWidth(200f);
        setRes.setHeight(20f);
        setRes.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 40f);
        setRes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector2 res = getRes(resolutions);
                resInd = resolutions.getSelectedIndex();
                Gdx.graphics.setWindowedMode((int) res.x, (int) res.y);
                resize((int) res.x, (int) res.y);
                options();
            }
        });



        stage.clear();
        stage.addActor(resolutions);
        stage.addActor(back);
        stage.addActor(setRes);
        stage.addActor(windowed);
    }

    public void instructions() {
        final TextButton back = new TextButton("Return", skin, "default");
        back.setWidth(200f);
        back.setHeight(20f);
        back.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                mainMenu();
            }
        });

        Label title = new Label("How to play: Do it just like Warcraft 3", skin);
        title.setWidth(300);
        title.setAlignment(Align.center);
        title.setPosition(Gdx.graphics.getWidth() /2 - 150f, Gdx.graphics.getHeight()/2 - 40f);

        stage.clear();
        stage.addActor(back);
        stage.addActor(title);
    }

    public void mainMenu() {
        final TextButton start = new TextButton("Start Game", skin, "default");
        start.setWidth(200f);
        start.setHeight(20f);
        start.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);
        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                setScreen();
            }
        });

        final TextButton instructions = new TextButton("Instructions", skin, "default");
        instructions.setWidth(200f);
        instructions.setHeight(20f);
        instructions.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 40f);
        instructions.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                instructions();
            }
        });

        final TextButton options = new TextButton("Options", skin, "default");
        options.setWidth(200f);
        options.setHeight(20f);
        options.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 70f);
        options.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                options();
            }
        });

        stage.clear();
        stage.addActor(start);
        stage.addActor(instructions);
        stage.addActor(options);
    }

    public void setScreen() {
        game.setScreen(new GameScreen(game));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.setProjectionMatrix(matrix);
        stage.act(delta);
        game.batch.begin();
        stage.draw();
        game.batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false);
        game.batch.setProjectionMatrix(camera.combined);
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
        stage.dispose();
    }
}
