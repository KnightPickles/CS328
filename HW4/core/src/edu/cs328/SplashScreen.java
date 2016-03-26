package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by KnightPickles on 3/26/16.
 */
public class SplashScreen implements Screen {

    private HW4 game;
    private Stage stage;
    private Skin skin;
    private Label title;

    public SplashScreen(HW4 game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        mainMenu();
    }

    public void options() {
        /*SelectBox resolutions = new SelectBox(skin);
        resolutions.setHeight(20f);
        resolutions.setWidth(200f);
        resolutions.setItems(new String[] {"1080x1920", "720x1280", "640x480"});
        resolutions.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 70f);*/

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
        setRes.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(640, 480);
                resize(640, 480);
            }
        });

        stage.clear();
        //stage.addActor(resolutions);
        stage.addActor(back);
        stage.addActor(setRes);
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

        stage.clear();
        stage.addActor(back);
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
        stage.act(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
