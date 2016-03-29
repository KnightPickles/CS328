package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 3/27/2016.
 */
public class GameGUI {
    private HW4 game;
    public Stage stage;
    private Skin skin;
    private OrthographicCamera camera;
    private Batch gameEnd;
    private SelectionManager sm;
    private ArrayList<GhostComponent> gcl = new ArrayList<GhostComponent>();
    private boolean dispG = false;
    private boolean dispB = false;
    private boolean dispMux = false;
    private boolean display = false;
    private Sprite victory;
    private Window gp, bp, cw, rs;
    private TextField f;

    public enum commandType {
    	None,
    	Rally,
    	Action,
    	Patrol
    }
    
    GameGUI(HW4 game, SelectionManager manager) {
        this.game = game;
        gameEnd = new SpriteBatch();
        sm = manager;
        victory = game.atlas.createSprite("victory");
        victory.setSize(victory.getWidth() * (HW4.SCALE * 2 * HW4.SCALE + 1), victory.getHeight() * (HW4.SCALE * 2 * HW4.SCALE + 1));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        gp = ghostPanel();
        bp = structurePanel();
        f = new TextField("Souls: " + GhostComponent.money, skin);
        cw = currencyScreen();
        rs = returnScreen();
    }

    public void victory() {
        victory.setPosition(-victory.getWidth() / 2 + Gdx.graphics.getWidth() / 2, -victory.getHeight() / 2 + 50 * HW4.SCALE + Gdx.graphics.getHeight() / 2);
        gameEnd.begin();
        victory.draw(gameEnd);
        gameEnd.end();
        returnScreen();
    }

    public void defeat() {
        returnScreen();
    }

    public Window currencyScreen() {
        Window win = new Window("Information", skin);
        win.setWidth(200);
        win.setHeight(40);
        win.setMovable(true);
        win.setPosition(0, 0);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(f);
        return win;
    }

    public Window returnScreen() {
        final TextButton ts = new TextButton("Return to Title Screen", skin, "default");
        ts.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SplashScreen(game));
            }
        });

        final TextButton qd = new TextButton("Quit to Desktop", skin, "default");
        qd.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        final Window win = new Window("Return Menu", skin);
        win.setWidth(200);
        win.setHeight(90);
        win.setMovable(true);
        win.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - win.getHeight() / 2);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(ts);
        win.row().fill();
        win.add(qd);
        return win;
    }

    public Window structurePanel() {
        if(HW4.stop) return null;

        float offset = 100;

        final TextButton ral = new TextButton("Set Rally", skin, "default");
        final Color c = ral.getColor();
        ral.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ral.setColor(Color.DARK_GRAY); // set to white to get color back
                SelectionManager._instance.setCurrCommand(commandType.Rally, ral);
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
        worker.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EntityManager._instance.alliedBase.getComponent(BuildingComponent.class).trainWorkerUnit();
            }
        });

        final Window win = new Window("Home Base Actions", skin);
        win.setWidth(500);
        win.setHeight(90);
        win.setMovable(false);
        win.setPosition(Gdx.graphics.getWidth() / 2 - 250, 0);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(ral);
        win.add(melee);
        win.add(ranged);
        win.row().fill();
        win.add(train);
        win.add(worker);

        return win;
    }

    public void resetColor(TextButton button) {
        if(button != null) button.setColor(Color.WHITE);
    }

    public Window ghostPanel() {
        if(HW4.stop) return null;

        float offset = 100;

        final TextButton atk = new TextButton("Action", skin, "default");
        final TextButton pat = new TextButton("Patrol", skin, "default");
        final TextButton def = new TextButton("Defend", skin, "default");
        final TextButton upg = new TextButton("Flee", skin, "default");
        
        atk.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                atk.setColor(Color.DARK_GRAY);// set to white to get color back
                pat.setColor(Color.WHITE);
                SelectionManager._instance.setCurrCommand(commandType.Action, atk);
                // waitForLeftMouse(atk);
                return true;
            }
        });

        def.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                SelectionManager._instance.issueDefendCommand();
            }
        });

        pat.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                pat.setColor(Color.DARK_GRAY); // set to white to get color back
                atk.setColor(Color.WHITE);
                SelectionManager._instance.setCurrCommand(commandType.Patrol, pat);
                return true;
            }
        });
 
        upg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
            	SelectionManager._instance.issueFleeCommand();
            }
        });

        final Window win = new Window("Ghost Unit Actions", skin);
        win.setWidth(230);
        win.setHeight(90);
        win.setMovable(false);
        win.setPosition(Gdx.graphics.getWidth() / 2 - 115, 0);
        win.defaults().space(5);
        win.row().fill().expandX();
        win.add(atk, def);
        win.row().fill();
        win.add(pat, upg);

        return win;
    }

    public void update() {
        gcl = new ArrayList<GhostComponent>();
        if(SelectionManager._instance.singleSelected != null) {
            BuildingComponent bc = sm.singleSelected.getComponent(BuildingComponent.class);
            GhostComponent gc = sm.singleSelected.getComponent(GhostComponent.class);
            if(bc != null && bc.bc.playerControlled) {
                if(bp.getStage() == null) {
                    stage.clear();
                    stage.addActor(bp);
                }
            } else if(gc != null && gc.bc.playerControlled) {
                gcl.add(gc);
                if(gp.getStage() == null) {
                    stage.clear();
                    stage.addActor(gp);
                }
            }
        } else if(SelectionManager._instance.selected.size() > 0) {
            for(Entity e : sm.selected) {
                GhostComponent gc = e.getComponent(GhostComponent.class);
                if (gc != null)
                    gcl.add(gc);
            }
            if(gp.getStage() == null) {
                stage.clear();
                stage.addActor(gp);
            }
        } else {
            stage.clear();
        }
        f.setText("Souls: " + GhostComponent.money);
        if(cw.getStage() == null) stage.addActor(cw);
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            display = !display;
        }
        if(HW4.win && rs.getStage() == null) stage.addActor(rs);
    }

    public void render() {
        if(HW4.stop) {
            if(HW4.win) victory();
            else defeat();
        }

        //returnScreen();
        game.batch.setProjectionMatrix(camera.combined);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
