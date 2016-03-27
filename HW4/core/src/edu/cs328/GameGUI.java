package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by KnightPickles on 3/27/2016.
 */
public class GameGUI {
    private HW4 game;
    public Stage stage;
    private Skin skin;
    private OrthographicCamera camera;

    GameGUI(HW4 game) {
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth() , Gdx.graphics.getHeight(), camera));
        Gdx.input.setInputProcessor(stage);
        panel();
    }

    public void panel() {
        float offset = 100;

        final TextButton atk = new TextButton("Action", skin, "default");
        atk.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){

            }
        });
        final TextButton def = new TextButton("Defend", skin, "default");
        def.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){

            }
        });
        final TextButton pat = new TextButton("Patrol", skin, "default");
        pat.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){

            }
        });
        final TextButton upg = new TextButton("Upgrade", skin, "default");
        upg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){

            }
        });

        final Window win = new Window("Unit Actions", skin);
        win.setPosition(Gdx.graphics.getWidth() /2 + offset - 5, 0);
        win.setWidth(225);
        win.setHeight(90);
       // win.setMovable(false);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(atk, def);
        win.row().fill();
        win.add(pat, upg);


        stage.clear();
        stage.addActor(win);
    }

    public void render() {
        game.batch.setProjectionMatrix(camera.combined);
        stage.act(Gdx.graphics.getDeltaTime());
        game.batch.begin();
        stage.draw();
        game.batch.end();
    }
}
