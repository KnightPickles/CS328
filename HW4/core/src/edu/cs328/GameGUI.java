package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 3/27/2016.
 */
public class GameGUI {
    private HW4 game;
    public Stage stage;
    private Skin skin;
    private OrthographicCamera camera;
    private SelectionManager sm;
    private ArrayList<BuildingComponent> bcl = new ArrayList<BuildingComponent>();
    private ArrayList<GhostComponent> gcl = new ArrayList<GhostComponent>();
    private boolean dispG = false;
    private boolean dispB = false;
    private boolean dispMux = false;

    GameGUI(HW4 game, SelectionManager manager) {
        this.game = game;
        sm = manager;
        camera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth() , Gdx.graphics.getHeight(), camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        ghostPanel();
    }

    public void structurePanel() {
        if(dispB) return;
        dispB = true;
        dispG = false;

        float offset = 100;

        final TextButton ral = new TextButton("Rally Point", skin, "default");
        ral.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                // for blist: rally();
            }
        });
        final TextButton train = new TextButton("Manifest Ghost", skin, "default");
        train.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                // for blist: trainUnit();
            }
        });

        final Window win = new Window("Unit Actions", skin);
        win.setPosition(Gdx.graphics.getWidth() /2 + offset - 5, 0);
        win.setWidth(225);
        win.setHeight(90);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(ral);
        win.row().fill();
        win.add(train);

        stage.clear();
        stage.addActor(win);
    }

    public void ghostPanel() {
        if(dispG) return;
        dispG = true;
        dispB = false;

        float offset = 100;

        final TextButton atk = new TextButton("Action", skin, "default");
        atk.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                // for glist: attackState()
            }
        });
        final TextButton def = new TextButton("Defend", skin, "default");
        def.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                // for glist: defendState()
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
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(atk, def);
        win.row().fill();
        win.add(pat, upg);

        stage.clear();
        stage.addActor(win);
    }

    public void update() {
        bcl = new ArrayList<BuildingComponent>();
        gcl = new ArrayList<GhostComponent>();
        if(sm.singleSelected != null) {
            BuildingComponent bc = sm.singleSelected.getComponent(BuildingComponent.class);
            GhostComponent gc = sm.singleSelected.getComponent(GhostComponent.class);
            if(bc != null && bc.bc.playerControlled) {
                bcl.add(bc);
                structurePanel();
            } else if(gc != null && gc.bc.playerControlled) {
                gcl.add(gc);
                ghostPanel();
            }
        } else if(sm.tempSelected != null) {
            for(Entity e : sm.selected) {
                BuildingComponent bc = e.getComponent(BuildingComponent.class);
                GhostComponent gc = e.getComponent(GhostComponent.class);
                if (bc != null)
                    bcl.add(bc);
                if (gc != null)
                    gcl.add(gc);
            }
            if(gcl.size() > 0) {
                ghostPanel();
            } else {
                structurePanel();
            }
        }
    }

    public void render() {
        game.batch.setProjectionMatrix(camera.combined);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
