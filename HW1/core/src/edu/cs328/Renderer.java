package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class Renderer {

    private final static boolean debugRender = false;
    private Box2DDebugRenderer debugRenderer;

    //--------------------------------------

    private Game game;
    private GameScreen gameScreen;
    private World box2dWorld;
    private TiledMap map;
    private SpriteBatch batch;
    private OrthographicCamera GAMEcam;
    private OrthographicCamera HUDcam;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float ppm;

    public float winkel;
    private DynamicGameObject boxObject;
    private TextureRegion box;
    public StringBuffer stringBuffer;
    private int i;
    //--------------------------------------


    public Renderer(Game game, GameScreen gameScreen) {

        this.gameScreen = gameScreen;
        this.game = game;
        this.batch 		= game.getSpritebatch();
        this.box2dWorld = gameScreen.box2dWorld;
        this.map 		= gameScreen.map;

        GAMEcam = game.getGAMECamera();
        HUDcam	= game.getHUDCamera();


        ppm = 1 / game.getPPM();
        Gdx.app.log("game.getPPM(): " + game.getPPM(), "ppm: " +ppm);

        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        mapRenderer = new OrthogonalTiledMapRenderer(map, ppm, batch);

        box = new TextureRegion(Assets.dummyFire, 20, 10, 1, 1);
        stringBuffer = new StringBuffer();

        //-------------------------------------------------
        if (debugRender) debugRenderer = new Box2DDebugRenderer();
    }




    public void renderTiledMap () {

        //---- move tiledmap-camera to the middle of the playfield -----------------
        GAMEcam.position.x = (GameCore.VIRTUAL_WIDTH_GAME / 2f) -
                ((GameCore.VIRTUAL_WIDTH_GAME / 2f) - (GameCore.PLAYFIELDWIDTH / 2f));
        GAMEcam.position.y = (GameCore.VIRTUAL_HEIGHT_GAME / 2f) -
                ((GameCore.VIRTUAL_HEIGHT_GAME / 2f) - (GameCore.PLAYFIELDHEIGHT / 2f));
        GAMEcam.update();

        //---- render tiledmap -----------------
        mapRenderer.setView(GAMEcam);
        mapRenderer.render();

        //---- move tiledmap-camera back to the middle of the viewport for correct unproject-coords -----------------
        GAMEcam.position.x = GameCore.VIRTUAL_WIDTH_GAME / 2f;
        GAMEcam.position.y = GameCore.VIRTUAL_HEIGHT_GAME / 2f;
    }




    //------- render game-stuff ---------------------
    public void renderGamePlay () {

        game.updateGAMECam();

        batch.begin();
        batch.enableBlending();

        for (i = 0; i < GameScreen.BOXCOUNT; i++) {

            boxObject = gameScreen.boxes.get(i);

            if (boxObject.body.isActive()) {
                winkel =  (MathUtils.radiansToDegrees * boxObject.angle);
                batch.draw(box, boxObject.pos.x, boxObject.pos.y, 0, 0, boxObject.width, boxObject.height, 1, 1, winkel);
            }
        }

        batch.end();

        //----- Debugging ---------------------------
        if (debugRender) debugRenderer.render(box2dWorld, game.getHUDCamera().combined.scale(game.getPPM(), game.getPPM(), game.getPPM()));
    }


    //------- render HUD-stuff ---------------------
    public void renderHUD () {

        game.updateHUDCam();
        batch.begin();

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("Zoom: ").append(1/game.getHUDCamera().zoom);
        Assets.font.getData().setScale(1f);
        Assets.font.draw(batch, stringBuffer, 160, 380);

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("lastFPS: ").append(gameScreen.lastFPS);
        Assets.font.getData().setScale(1 + ((60 - gameScreen.lastFPS) / 20f));
        Assets.font.draw(batch, stringBuffer, 400, 380);

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("logic_lastFPS: ").append(gameScreen.logic_lastFPS);
        Assets.font.getData().setScale(1f);
        Assets.font.draw(batch, stringBuffer, 510, 380);

        batch.end();
    }
}
