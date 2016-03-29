package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
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
    private ArrayList<GhostComponent> gcl = new ArrayList<GhostComponent>();
    private boolean dispG = false;
    private boolean dispB = false;
    private boolean dispMux = false;
    boolean selected = false;

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

        final TextButton ral = new TextButton("Set Rally", skin, "default");
        final Color c = ral.getColor();
        ral.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ral.setColor(Color.DARK_GRAY); // set to white to get color back
                return true;
            }
        });

        ral.setChecked(true);

        final TextButton train = new TextButton("Upgrade Units", skin, "default");
        train.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ral.setColor(Color.WHITE);
                EntityManager._instance.alliedBase.getComponent(BuildingComponent.class).increaseUpgradeLevel();
            }
        });

        final TextButton melee = new TextButton("Manifest Melee Unit", skin, "default");
        melee.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.alliedBase.getComponent(BuildingComponent.class).trainMeleeUnit();
            }
        });
        final TextButton ranged = new TextButton("Manifest Ranged Unit", skin, "default");
        ranged.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.alliedBase.getComponent(BuildingComponent.class).trainRangedUnit();
            }
        });
        final TextButton worker = new TextButton("Manifest Worker Unit", skin, "default");
        ranged.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.alliedBase.getComponent(BuildingComponent.class).trainWorkerUnit();
            }
        });

        final Window win = new Window("Unit Actions", skin);
        win.setWidth(500);
        win.setHeight(90);
        win.setPosition(Gdx.graphics.getWidth() / 2 - win.getPrefWidth() / 2, 0);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(ral);
        win.add(melee);
        win.add(ranged);
        win.row().fill();
        win.add(train);
        win.add(worker);

        stage.clear();
        stage.addActor(win);
    }

    public void resetColor(TextButton button) {
        if(button != null) button.setColor(Color.WHITE);
    }

    public void ghostPanel() {
        if(dispG) return;
        dispG = true;
        dispB = false;

        float offset = 100;

        final TextButton atk = new TextButton("Action", skin, "default");
        atk.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                atk.setColor(Color.DARK_GRAY);// set to white to get color back
                // waitForLeftMouse(atk);
                return true;
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
        pat.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                pat.setColor(Color.DARK_GRAY); // set to white to get color back
                return true;
            }
        });
        final TextButton upg = new TextButton("Flee", skin, "default");
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
        gcl = new ArrayList<GhostComponent>();
        if(sm.singleSelected != null) {
            BuildingComponent bc = sm.singleSelected.getComponent(BuildingComponent.class);
            GhostComponent gc = sm.singleSelected.getComponent(GhostComponent.class);
            if(bc != null && bc.bc.playerControlled) {
                structurePanel();
            } else if(gc != null && gc.bc.playerControlled) {
                gcl.add(gc);
                ghostPanel();
            }
        } else if(sm.tempSelected != null) {
            for(Entity e : sm.selected) {
                GhostComponent gc = e.getComponent(GhostComponent.class);
                if (gc != null)
                    gcl.add(gc);
            }
            //if(gcl.size() > 0) {
                ghostPanel();
              //  structurePanel();
           // }
        }
    }

    public void render() {
        game.batch.setProjectionMatrix(camera.combined);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
